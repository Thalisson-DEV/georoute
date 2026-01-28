package com.sipel.backend.mappers;

import com.sipel.backend.domain.Clientes;
import com.sipel.backend.dtos.ClientesRequestDTO;
import com.sipel.backend.dtos.ClientesResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ClientesMapperTest {

    private final ClientesMapper mapper = Mappers.getMapper(ClientesMapper.class);

    @Test
    @DisplayName("Should map DTO Request to Entity")
    void shouldMapDtoToEntity() {
        ClientesRequestDTO dto = new ClientesRequestDTO(123L, 456L, 789L, "P123", "Teste", -23.0, -46.0);

        Clientes entity = mapper.dtoRequestToEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getInstalacao(), entity.getInstalacao());
        assertEquals(dto.getContaContrato(), entity.getContaContrato());
        assertEquals(dto.getNomeCliente(), entity.getNomeCliente());
    }

    @Test
    @DisplayName("Should map Entity to DTO Response")
    void shouldMapEntityToDtoResponse() {
        Clientes entity = new Clientes(123L, 456L, 789L, "P123", "Teste", -23.0, -46.0);

        ClientesResponseDTO dto = mapper.entityToDtoResponse(entity);

        assertNotNull(dto);
        assertEquals(entity.getInstalacao(), dto.instalacao());
        assertEquals(entity.getNomeCliente(), dto.nomeCliente());
    }
}
