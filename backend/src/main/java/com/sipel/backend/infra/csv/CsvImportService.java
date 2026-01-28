package com.sipel.backend.infra.csv;

import com.sipel.backend.exceptions.CsvImportException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class CsvImportService {

    private final CsvAsyncService csvAsyncService;

    public CsvImportService(CsvAsyncService csvAsyncService) {
        this.csvAsyncService = csvAsyncService;
    }

    public void InitializeImport(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            throw new CsvImportException("O arquivo CSV está vazio.");
        }

        String filename = multipartFile.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new CsvImportException("O arquivo deve ter a extensão .csv");
        }

        String contentType = multipartFile.getContentType();
        if (contentType != null && !isCsvContentType(contentType)) {
           log.warn("Content-Type suspeito recebido: {}", contentType);
           throw new CsvImportException("Formato de arquivo inválido.");
        }

        java.io.File tempFile = java.io.File.createTempFile("import-", ".csv");
        multipartFile.transferTo(tempFile);

        csvAsyncService.processarCsvAsync(tempFile);
    }

    private boolean isCsvContentType(String contentType) {
        return contentType.equals("text/csv") || 
               contentType.equals("application/vnd.ms-excel") || 
               contentType.equals("text/plain");
    }
}
