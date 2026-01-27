package com.sipel.backend.mappers;

import com.sipel.backend.domain.Clientes;
import com.sipel.backend.dtos.ClientesRequestDTO;
import com.sipel.backend.dtos.ClientesResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientesMapper {

    Clientes dtoRequestToEntity(ClientesRequestDTO dto);

    ClientesResponseDTO entityToDtoResponse(Clientes entity);
}
