package com.viatico.proyect.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.viatico.proyect.entity.*;
import com.viatico.proyect.service.interfaces.LiquidacionService;
import com.viatico.proyect.service.interfaces.PdfService;
import com.viatico.proyect.service.interfaces.RendicionService;
import com.viatico.proyect.service.interfaces.SolicitudService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.lowagie.text.Rectangle;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private final SolicitudService solicitudService;
    private final RendicionService rendicionService;
    private final LiquidacionService liquidacionService;

    @Override
    public ByteArrayInputStream generarReporteLiquidacion(Long solicitudId) {
        SolicitudComision sol = solicitudService.obtenerPorId(solicitudId);
        RendicionCuentas ren = rendicionService.obtenerPorSolicitud(solicitudId);
        LiquidacionFinal liq = liquidacionService.obtenerPorSolicitud(solicitudId);

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fuentes
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.BLACK);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);

            // Título
            Paragraph title = new Paragraph("LIQUIDACIÓN DE VIÁTICOS", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Información General
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(10);
            infoTable.setSpacingAfter(20);

            addInfoCell(infoTable, "Solicitante:",
                    sol.getEmpleado().getNombres() + " " + sol.getEmpleado().getApellidos(), boldFont, normalFont);
            addInfoCell(infoTable, "ID Solicitud:", "#" + sol.getId(), boldFont, normalFont);
            addInfoCell(infoTable, "Motivo:", sol.getMotivoViaje(), boldFont, normalFont);
            String destino = (sol.getItinerarios() != null && !sol.getItinerarios().isEmpty())
                    ? sol.getItinerarios().get(0).getZonaDestino().getNombre()
                    : "N/A";
            addInfoCell(infoTable, "Destino:", destino, boldFont, normalFont);
            addInfoCell(infoTable, "Periodo:", sol.getFechaInicio() + " al " + sol.getFechaFin(), boldFont, normalFont);
            addInfoCell(infoTable, "Estado:", sol.getEstado().toString(), boldFont, normalFont);

            document.add(infoTable);

            // Resumen Financiero
            Paragraph resTitle = new Paragraph("RESUMEN FINANCIERO", subtitleFont);
            resTitle.setSpacingAfter(10);
            document.add(resTitle);

            PdfPTable resTable = new PdfPTable(3);
            resTable.setWidthPercentage(100);
            resTable.setSpacingAfter(20);

            addNumericCell(resTable, "Monto Asignado", sol.getMontoTotal(), headerFont, boldFont,
                    new Color(0, 113, 227));
            addNumericCell(resTable, "Total Gastado", ren != null ? ren.getTotalAceptado() : BigDecimal.ZERO,
                    headerFont, boldFont, new Color(134, 134, 139));

            BigDecimal saldo = BigDecimal.ZERO;
            if (liq != null) {
                saldo = liq.getSaldoAfavorEmpleado().subtract(liq.getSaldoAfavorEmpresa());
            }
            Color saldoColor = saldo.signum() >= 0 ? new Color(52, 199, 89) : new Color(255, 59, 48);
            addNumericCell(resTable, "Saldo Final", saldo, headerFont, boldFont, saldoColor);

            document.add(resTable);

            // Detalle de Gastos
            if (ren != null && ren.getDetalles() != null) {
                Paragraph gastTitle = new Paragraph("DETALLE DE COMPROBANTES ACEPTADOS", subtitleFont);
                gastTitle.setSpacingAfter(10);
                document.add(gastTitle);

                PdfPTable detallesTable = new PdfPTable(new float[] { 3, 4, 3, 2, 2 });
                detallesTable.setWidthPercentage(100);

                String[] headers = { "Tipo de Gasto", "Proveedor", "N° Doc", "Fecha", "Monto" };
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                    cell.setBackgroundColor(Color.BLACK);
                    cell.setPadding(8);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    detallesTable.addCell(cell);
                }

                for (DetalleComprobante det : ren.getDetalles()) {
                    if (det.isValidado()) {
                        detallesTable.addCell(new PdfPCell(new Phrase(det.getTipoGasto().getNombre(), normalFont)));
                        detallesTable.addCell(new PdfPCell(new Phrase(det.getRazonSocialEmisor(), normalFont)));
                        detallesTable.addCell(new PdfPCell(
                                new Phrase(det.getSerieComprobante() + "-" + det.getNumeroComprobante(), normalFont)));
                        detallesTable.addCell(
                                new PdfPCell(new Phrase(det.getFechaEmision().toLocalDate().toString(), normalFont)));

                        PdfPCell priceCell = new PdfPCell(new Phrase("S/ " + det.getMontoTotal(), boldFont));
                        priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        detallesTable.addCell(priceCell);
                    }
                }
                document.add(detallesTable);
            }

            // Firmas
            document.add(new Paragraph("\n\n\n"));
            PdfPTable signTable = new PdfPTable(2);
            signTable.setWidthPercentage(100);

            PdfPCell sign1 = new PdfPCell(new Phrase("__________________________\nFirma del Colaborador", normalFont));
            sign1.setBorder(Rectangle.NO_BORDER);
            sign1.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell sign2 = new PdfPCell(
                    new Phrase("__________________________\nFirma de Administración", normalFont));
            sign2.setBorder(Rectangle.NO_BORDER);
            sign2.setHorizontalAlignment(Element.ALIGN_CENTER);

            signTable.addCell(sign1);
            signTable.addCell(sign2);
            document.add(signTable);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addInfoCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, labelFont));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellLabel.setPadding(5);
        table.addCell(cellLabel);

        PdfPCell cellValue = new PdfPCell(new Phrase(value, valueFont));
        cellValue.setBorder(Rectangle.NO_BORDER);
        cellValue.setPadding(5);
        table.addCell(cellValue);
    }

    private void addNumericCell(PdfPTable table, String label, BigDecimal value, Font headerFont, Font valueFont,
            Color bgColor) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bgColor);
        cell.setPadding(10);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph p1 = new Paragraph(label, headerFont);
        p1.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p1);

        Paragraph p2 = new Paragraph("S/ " + value, valueFont);
        p2.setAlignment(Element.ALIGN_CENTER);
        p2.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.WHITE));
        cell.addElement(p2);

        table.addCell(cell);
    }
}
