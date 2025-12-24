package com.viatico.proyect.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

public interface StorageService {
    String store(MultipartFile file);

    Resource loadAsResource(String filename);

    void deleteFile(String filename);
}
