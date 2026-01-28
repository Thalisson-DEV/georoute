package com.sipel.backend.exceptions;

public class CsvImportException extends RuntimeException {
    public CsvImportException(String message) {
        super(message);
    }
}
