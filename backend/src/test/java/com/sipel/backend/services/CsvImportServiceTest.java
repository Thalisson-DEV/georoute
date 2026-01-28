package com.sipel.backend.services;

import com.sipel.backend.exceptions.CsvImportException;
import com.sipel.backend.infra.csv.CsvAsyncService;
import com.sipel.backend.infra.csv.CsvImportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvImportServiceTest {

    @Mock
    private CsvAsyncService csvAsyncService;

    @InjectMocks
    private CsvImportService csvImportService;

    @Test
    void testInitializeImport_Success() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv", "content".getBytes()
        );

        // Act
        csvImportService.InitializeImport(file);

        // Assert
        verify(csvAsyncService, times(1)).processarCsvAsync(any(File.class));
    }

    @Test
    void testInitializeImport_EmptyFile() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.csv", "text/csv", new byte[0]
        );

        // Act & Assert
        assertThrows(CsvImportException.class, () -> csvImportService.InitializeImport(file));
        verifyNoInteractions(csvAsyncService);
    }
}