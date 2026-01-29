package com.sipel.backend.dtos;

import java.util.List;

public record PaginatedClientesResponseDTO(
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,

        List<ClienteResponseDTO> data
) {}
