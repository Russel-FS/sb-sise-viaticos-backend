package com.viatico.proyect.service.impl;

import com.viatico.proyect.entity.SolicitudComision;
import com.viatico.proyect.service.interfaces.ExcelService;
import com.viatico.proyect.service.interfaces.SolicitudService;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    private final SolicitudService solicitudService;

    @Override
    public ByteArrayInputStream generarReporteSolicitudes() {
        String[] columns = { "ID", "Solicitante", "Motivo", "Fecha Inicio", "Fecha Fin", "Monto Asignado", "Estado",
                "Fecha Solicitud" };

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Solicitudes");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
                cell.setCellStyle(headerCellStyle);
            }

            List<SolicitudComision> solicitudes = solicitudService.listarTodas();

            int rowIdx = 1;
            for (SolicitudComision sol : solicitudes) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(sol.getId());
                row.createCell(1).setCellValue(sol.getEmpleado().getNombres() + " " + sol.getEmpleado().getApellidos());
                row.createCell(2).setCellValue(sol.getMotivoViaje());
                row.createCell(3).setCellValue(sol.getFechaInicio().toString());
                row.createCell(4).setCellValue(sol.getFechaFin().toString());
                row.createCell(5).setCellValue("S/ " + sol.getMontoTotal());
                row.createCell(6).setCellValue(sol.getEstado().toString());
                row.createCell(7)
                        .setCellValue(sol.getFechaSolicitud() != null ? sol.getFechaSolicitud().toString() : "");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error al generar Excel: " + e.getMessage());
        }
    }
}
