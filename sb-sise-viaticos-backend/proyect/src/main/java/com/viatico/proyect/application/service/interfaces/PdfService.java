package com.viatico.proyect.application.service.interfaces;

import java.io.ByteArrayInputStream;

public interface PdfService {
    ByteArrayInputStream generarReporteLiquidacion(Long solicitudId);
}
