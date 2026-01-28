package com.sipel.backend.dtos;

public record ClientesResponseDTO(
        Long instalacao,
        String nomeCliente,
        Double latitude,
        Double longitude
) {}
