package com.viatico.proyect.service.interfaces;

import java.io.ByteArrayInputStream;

public interface PdfService {
    ByteArrayInputStream generarReporteLiquidacion(Long solicitudId);
}
