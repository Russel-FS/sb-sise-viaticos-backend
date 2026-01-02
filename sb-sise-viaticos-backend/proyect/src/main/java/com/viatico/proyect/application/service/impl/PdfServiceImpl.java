package com.viatico.proyect.application.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.viatico.proyect.application.service.interfaces.LiquidacionService;
import com.viatico.proyect.application.service.interfaces.PdfService;
import com.viatico.proyect.application.service.interfaces.RendicionService;
import com.viatico.proyect.application.service.interfaces.SolicitudService;
import com.viatico.proyect.domain.entity.*;
import com.viatico.proyect.domain.enums.EstadoComprobante;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private final SolicitudService solicitudService;
    private final RendicionService rendicionService;
    private final LiquidacionService liquidacionService;

    private static final Color COL_TEXT_PRIMARY = new Color(29, 29, 31); // #1d1d1f
    private static final Color COL_TEXT_SECONDARY = new Color(134, 134, 139); // #86868b
    private static final Color COL_BG_STRIP = new Color(245, 245, 247); // #f5f5f7
    private static final Color COL_BORDER_LIGHT = new Color(230, 230, 230);
    private static final Color COL_SUCCESS = new Color(52, 199, 89);
    private static final Color COL_DANGER = new Color(255, 59, 48);

    @Override
    public ByteArrayInputStream generarReporteLiquidacion(Long solicitudId) {
        SolicitudComision sol = solicitudService.obtenerPorId(solicitudId);
        RendicionCuentas ren = rendicionService.obtenerPorSolicitud(solicitudId);
        LiquidacionFinal liq = liquidacionService.obtenerPorSolicitud(solicitudId);

        // A4 estándar con márgenes generosos
        Document document = new Document(PageSize.A4, 50, 50, 60, 60);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fDisplayBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, COL_TEXT_PRIMARY);
            Font fHeading = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COL_TEXT_PRIMARY);
            Font fLabel = FontFactory.getFont(FontFactory.HELVETICA, 8, COL_TEXT_SECONDARY);

            Font fBody = FontFactory.getFont(FontFactory.HELVETICA, 10, COL_TEXT_PRIMARY);
            Font fBodyBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COL_TEXT_PRIMARY);

            Font fTableHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, COL_TEXT_SECONDARY);

            Font fBigNumber = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, COL_TEXT_PRIMARY);

            PdfPTable topBar = new PdfPTable(2);
            topBar.setWidthPercentage(100);

            PdfPCell cBrand = new PdfPCell(
                    new Phrase("ISS VIÁTICOS", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, COL_TEXT_SECONDARY)));
            cBrand.setBorder(Rectangle.NO_BORDER);
            cBrand.setVerticalAlignment(Element.ALIGN_MIDDLE);
            topBar.addCell(cBrand);

            // ID Comprobante
            PdfPCell cRef = new PdfPCell(new Phrase("Ref: " + String.format("%06d", sol.getId()), fLabel));
            cRef.setBorder(Rectangle.NO_BORDER);
            cRef.setHorizontalAlignment(Element.ALIGN_RIGHT);
            topBar.addCell(cRef);

            document.add(topBar);

            // Separador sutil
            PdfPTable line = new PdfPTable(1);
            line.setWidthPercentage(100);
            PdfPCell cLine = new PdfPCell();
            cLine.setBorder(Rectangle.BOTTOM);
            cLine.setBorderColor(COL_BORDER_LIGHT);
            cLine.setFixedHeight(10f);
            line.addCell(cLine);
            document.add(line);

            document.add(new Paragraph("\n"));

            // header
            Paragraph title = new Paragraph("Liquidación Final", fDisplayBold);
            title.setSpacingAfter(4);
            document.add(title);

            String rangoFechas = "Fecha no definida";
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d MMM yyyy");
            if (sol.getFechaInicio() != null) {
                rangoFechas = sol.getFechaInicio().format(fmt);
                if (sol.getFechaFin() != null) {
                    rangoFechas += " — " + sol.getFechaFin().format(fmt);
                }
            }

            Paragraph subtitle = new Paragraph(sol.getMotivoViaje() + " (" + rangoFechas + ")",
                    FontFactory.getFont(FontFactory.HELVETICA, 11, COL_TEXT_SECONDARY));
            subtitle.setSpacingAfter(30);
            document.add(subtitle);

            // empleado info
            PdfPTable empTable = new PdfPTable(2);
            empTable.setWidthPercentage(100);
            empTable.setSpacingAfter(30);

            // Empleado
            PdfPCell cEmp = new PdfPCell();
            cEmp.setBorder(Rectangle.NO_BORDER);
            cEmp.addElement(new Paragraph("EMPLEADO", fLabel));
            cEmp.addElement(
                    new Paragraph(sol.getEmpleado().getNombres() + " " + sol.getEmpleado().getApellidos(), fBodyBold));
            empTable.addCell(cEmp);

            // Cargo/Nivel
            PdfPCell cCargo = new PdfPCell();
            cCargo.setBorder(Rectangle.NO_BORDER);
            cCargo.addElement(new Paragraph("NIVEL / CARGO", fLabel));
            cCargo.addElement(new Paragraph(safeString(sol.getEmpleado().getNivel().getNombre()), fBody));
            empTable.addCell(cCargo);

            document.add(empTable);

            // financial strip
            PdfPTable strip = new PdfPTable(3);
            strip.setWidthPercentage(100);
            strip.setSpacingAfter(40);

            // Asignado
            addStripCell(strip, "ASIGNADO", safeBigDecimal(sol.getMontoTotal()), COL_TEXT_PRIMARY, fLabel, fBigNumber);

            // Gastado
            BigDecimal gastado = (ren != null) ? safeBigDecimal(ren.getDetalles().stream()
                    .filter(d -> d.getEstadoValidacion() == EstadoComprobante.ACEPTADO)
                    .map(DetalleComprobante::getMontoTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)) : BigDecimal.ZERO;
            addStripCell(strip, "GASTADO", gastado, COL_TEXT_PRIMARY, fLabel, fBigNumber);

            // Balance
            BigDecimal balance = BigDecimal.ZERO;
            if (liq != null) {
                balance = safeBigDecimal(liq.getSaldoAfavorEmpleado())
                        .subtract(safeBigDecimal(liq.getSaldoAfavorEmpresa()));
            }
            Color balColor = (balance.compareTo(BigDecimal.ZERO) >= 0) ? COL_SUCCESS : COL_DANGER;
            addStripCell(strip, "BALANCE", balance, balColor, fLabel, fBigNumber);

            document.add(strip);

            // lista de gastos
            if (ren != null && ren.getDetalles() != null && !ren.getDetalles().isEmpty()) {
                Paragraph listHeader = new Paragraph("Desglose de Gastos", fHeading);
                listHeader.setSpacingAfter(15);
                document.add(listHeader);

                PdfPTable table = new PdfPTable(new float[] { 3, 4, 3, 2, 2 });
                table.setWidthPercentage(100);

                // Headers
                addHeaderCell(table, "TIPO", fTableHeader);
                addHeaderCell(table, "PROVEEDOR", fTableHeader);
                addHeaderCell(table, "DOCUMENTO", fTableHeader);
                addHeaderCell(table, "FECHA", fTableHeader);
                addRightHeaderCell(table, "MONTO", fTableHeader);

                // filas
                for (DetalleComprobante det : ren.getDetalles()) {
                    if (det != null && det.getEstadoValidacion() == EstadoComprobante.ACEPTADO) {
                        String tipo = (det.getTipoGasto() != null) ? det.getTipoGasto().getNombre() : "-";
                        String prov = safeString(det.getRazonSocialEmisor());
                        if (prov.length() > 20)
                            prov = prov.substring(0, 20) + "...";

                        String doc = safeString(det.getSerieComprobante()) + "-"
                                + safeString(det.getNumeroComprobante());
                        String fec = (det.getFechaEmision() != null) ? det.getFechaEmision().toLocalDate().toString()
                                : "-";

                        addBodyCell(table, tipo, fBody);
                        addBodyCell(table, prov, fBody);
                        addBodyCell(table, doc, fBody);
                        addBodyCell(table, fec, fBody);
                        addRightBodyCell(table, "S/ " + safeBigDecimal(det.getMontoTotal()).toString(), fBodyBold);
                    }
                }

                document.add(table);
            }

            // footer / firmas
            document.add(new Paragraph("\n\n\n\n"));

            PdfPTable signatures = new PdfPTable(2);
            signatures.setWidthPercentage(100);

            PdfPCell s1 = new PdfPCell();
            s1.setBorder(Rectangle.NO_BORDER);
            s1.addElement(new Paragraph("______________________",
                    FontFactory.getFont(FontFactory.HELVETICA, 10, COL_TEXT_SECONDARY)));
            s1.addElement(new Paragraph("Firma del Empleado", fLabel));

            PdfPCell s2 = new PdfPCell();
            s2.setBorder(Rectangle.NO_BORDER);
            s2.addElement(new Paragraph("______________________",
                    FontFactory.getFont(FontFactory.HELVETICA, 10, COL_TEXT_SECONDARY)));
            s2.addElement(new Paragraph("V°B° Administración", fLabel));

            signatures.addCell(s1);
            signatures.addCell(s2);

            document.add(signatures);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // helpers

    private void addStripCell(PdfPTable table, String label, BigDecimal value, Color valColor, Font fLabel,
            Font fValueBase) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(COL_BG_STRIP);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(15);
        cell.setPaddingTop(20);
        cell.setPaddingBottom(20);

        // Label
        Paragraph pLbl = new Paragraph(label, fLabel);
        pLbl.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(pLbl);

        // Value
        Font fVal = new Font(fValueBase);
        fVal.setColor(valColor);
        Paragraph pVal = new Paragraph("S/ " + value.toString(), fVal);
        pVal.setAlignment(Element.ALIGN_CENTER);
        pVal.setSpacingBefore(4);
        cell.addElement(pVal);

        table.addCell(cell);
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(COL_BORDER_LIGHT);
        cell.setPaddingBottom(8);
        cell.setPaddingTop(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
    }

    private void addRightHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(COL_BORDER_LIGHT);
        cell.setPaddingBottom(8);
        cell.setPaddingTop(0);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(COL_BG_STRIP);
        cell.setPaddingTop(10);
        cell.setPaddingBottom(10);
        table.addCell(cell);
    }

    private void addRightBodyCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(COL_BG_STRIP);
        cell.setPaddingTop(10);
        cell.setPaddingBottom(10);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
    }

    private BigDecimal safeBigDecimal(BigDecimal val) {
        return val != null ? val : BigDecimal.ZERO;
    }

    private String safeString(String val) {
        return val != null ? val : "";
    }
}
