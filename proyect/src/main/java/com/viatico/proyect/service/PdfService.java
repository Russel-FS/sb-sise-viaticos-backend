package com.viatico.proyect.service;

import java.io.ByteArrayInputStream;

public interface PdfService {
    ByteArrayInputStream generarReporteLiquidacion(Long solicitudId);
}
