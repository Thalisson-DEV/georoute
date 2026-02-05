package com.sipel.backend.handlers;

import com.sipel.backend.dtos.RestExceptionResponseDTO;
import com.sipel.backend.exceptions.CsvImportException;
import com.sipel.backend.exceptions.EntityAlreadyExistsException;
import com.sipel.backend.exceptions.UserAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.io.IOException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(CsvImportException.class)
    public ResponseEntity<RestExceptionResponseDTO> handleCsvImportException(CsvImportException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        RestExceptionResponseDTO exceptionResponse = new RestExceptionResponseDTO(
                LocalDateTime.now(),
                e.getMessage(),
                status.value()
        );

        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<RestExceptionResponseDTO> handleIOException(IOException e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        RestExceptionResponseDTO exceptionResponse = new RestExceptionResponseDTO(
                LocalDateTime.now(),
                "Erro ao processar o arquivo: " + e.getMessage(),
                status.value()
        );

        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RestExceptionResponseDTO> handleEntityNotFoundException(EntityNotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        RestExceptionResponseDTO exceptionResponse = new RestExceptionResponseDTO(
                LocalDateTime.now(),
                e.getMessage(),
                status.value()
        );

        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<RestExceptionResponseDTO> handleEntityAlreadyExistsException(EntityAlreadyExistsException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        RestExceptionResponseDTO exceptionResponse = new RestExceptionResponseDTO(
                LocalDateTime.now(),
                e.getMessage(),
                status.value()
        );

        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<RestExceptionResponseDTO> handleMultipartException(MultipartException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        RestExceptionResponseDTO exceptionResponse = new RestExceptionResponseDTO(
                LocalDateTime.now(),
                e.getMessage(),
                status.value()
        );

        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<RestExceptionResponseDTO> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        RestExceptionResponseDTO exceptionResponse = new RestExceptionResponseDTO(
                LocalDateTime.now(),
                e.getMessage(),
                status.value()
        );

        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(com.fasterxml.jackson.core.JsonProcessingException.class)
    public ResponseEntity<RestExceptionResponseDTO> handleJsonProcessingException(com.fasterxml.jackson.core.JsonProcessingException e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        RestExceptionResponseDTO exceptionResponse = new RestExceptionResponseDTO(
                LocalDateTime.now(),
                "Erro ao processar JSON: " + e.getMessage(),
                status.value()
        );

        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @ExceptionHandler(org.springframework.web.reactive.function.client.WebClientResponseException.class)
    public ResponseEntity<RestExceptionResponseDTO> handleWebClientResponseException(org.springframework.web.reactive.function.client.WebClientResponseException e) {
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());

        RestExceptionResponseDTO exceptionResponse = new RestExceptionResponseDTO(
                LocalDateTime.now(),
                "Erro na API de Rotas: " + e.getMessage(),
                status.value()
        );

        return ResponseEntity.status(status).body(exceptionResponse);
    }

}
