package com.sipel.backend.services;

import com.sipel.backend.domain.Clientes;
import com.sipel.backend.dtos.ClientesRequestDTO;
import com.sipel.backend.dtos.ClientesResponseDTO;
import com.sipel.backend.exceptions.EntityAlreadyExistsException;
import com.sipel.backend.mappers.ClientesMapper;
import com.sipel.backend.repositories.ClientesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientesServiceTest {

    @Mock
    private ClientesRepository clientesRepository;

    @Mock
    private ClientesMapper clientesMapper;

    @InjectMocks
    private ClientesService clientesService;

    @Nested
    @DisplayName("Tests for saveCliente")
    class SaveClienteTests {

        @Test
        @DisplayName("Should save cliente successfully when it doesn't exist")
        void shouldSaveClienteSuccessfully() {
            ClientesRequestDTO request = new ClientesRequestDTO(123L, 456L, 789L, "P123", "Nome Teste", -23.123, -46.123);
            Clientes entity = new Clientes();

            when(clientesRepository.existsById(123L)).thenReturn(false);
            when(clientesMapper.dtoRequestToEntity(request)).thenReturn(entity);

            clientesService.saveCliente(request);

            verify(clientesRepository, times(1)).save(entity);
        }

        @Test
        @DisplayName("Should throw EntityAlreadyExistsException when cliente already exists")
        void shouldThrowExceptionWhenClienteExists() {
            ClientesRequestDTO request = new ClientesRequestDTO(123L, 456L, 789L, "P123", "Nome Teste", -23.123, -46.123);

            when(clientesRepository.existsById(123L)).thenReturn(true);

            assertThrows(EntityAlreadyExistsException.class, () -> clientesService.saveCliente(request));
            verify(clientesRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests for findClienteByInstalacao")
    class FindByInstalacaoTests {

        @Test
        @DisplayName("Should return ClientesResponseDTO when cliente exists")
        void shouldReturnResponseWhenExists() {
            Long id = 123L;
            Clientes entity = new Clientes();
            ClientesResponseDTO response = new ClientesResponseDTO(id, 456L, 789L, "P123", "Nome Teste", -23.123, -46.123);

            when(clientesRepository.findById(id)).thenReturn(Optional.of(entity));
            when(clientesMapper.entityToDtoResponse(entity)).thenReturn(response);

            ClientesResponseDTO result = clientesService.findClienteByInstalacao(id);

            assertNotNull(result);
            assertEquals(id, result.getInstalacao());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when cliente does not exist")
        void shouldThrowExceptionWhenNotFound() {
            Long id = 123L;
            when(clientesRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> clientesService.findClienteByInstalacao(id));
        }
    }

    @Nested
    @DisplayName("Tests for other search methods")
    class SearchMethodsTests {
        @Test
        @DisplayName("Should find by conta contrato")
        void shouldFindByContaContrato() {
            Long cc = 456L;
            Clientes entity = new Clientes();
            when(clientesRepository.findByContaContrato(cc)).thenReturn(Optional.of(entity));
            when(clientesMapper.entityToDtoResponse(entity)).thenReturn(new ClientesResponseDTO());

            assertNotNull(clientesService.findClienteByContaContrato(cc));
        }

        @Test
        @DisplayName("Should find by numero serie")
        void shouldFindByNumeroSerie() {
            Long ns = 789L;
            Clientes entity = new Clientes();
            when(clientesRepository.findByNumeroSerie(ns)).thenReturn(Optional.of(entity));
            when(clientesMapper.entityToDtoResponse(entity)).thenReturn(new ClientesResponseDTO());

            assertNotNull(clientesService.findClienteByNumeroSerie(ns));
        }

        @Test
        @DisplayName("Should find by numero poste")
        void shouldFindByNumeroPoste() {
            String np = "P123";
            Clientes entity = new Clientes();
            when(clientesRepository.findByNumeroPoste(np)).thenReturn(Optional.of(entity));
            when(clientesMapper.entityToDtoResponse(entity)).thenReturn(new ClientesResponseDTO());

            assertNotNull(clientesService.findClienteByNumeroPoste(np));
        }
    }
}
