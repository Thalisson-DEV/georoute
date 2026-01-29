package com.sipel.backend.mappers;

import com.sipel.backend.domain.Clientes;
import com.sipel.backend.dtos.ClienteResponseDTO;
import com.sipel.backend.dtos.ClientesRequestDTO;
import com.sipel.backend.dtos.PaginatedClientesResponseDTO;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ClientesMapper {

    Clientes dtoRequestToEntity(ClientesRequestDTO dto);

    ClienteResponseDTO entityToDtoResponse(Clientes entity);

    default PaginatedClientesResponseDTO entityToDtoPaginatedResponse(Page<Clientes> page) {
        return new PaginatedClientesResponseDTO(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getContent()
                        .stream()
                        .map(this::entityToDtoResponse)
                        .toList()

        );
    }
}
