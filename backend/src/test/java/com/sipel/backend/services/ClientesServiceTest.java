package com.sipel.backend.services;

import com.sipel.backend.domain.Clientes;
import com.sipel.backend.dtos.ClienteResponseDTO;
import com.sipel.backend.dtos.ClientesRequestDTO;
import com.sipel.backend.dtos.PaginatedClientesResponseDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
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
        @DisplayName("Should return ClienteResponseDTO when cliente exists")
        void shouldReturnResponseWhenExists() {
            Long id = 123L;
            Clientes entity = new Clientes();
            ClienteResponseDTO response = new ClienteResponseDTO(id, "Nome Teste", -23.123, -46.123);

            when(clientesRepository.findById(id)).thenReturn(Optional.of(entity));
            when(clientesMapper.entityToDtoResponse(entity)).thenReturn(response);

            ClienteResponseDTO result = clientesService.findClienteByInstalacao(id);

            assertNotNull(result);
            assertEquals(id, result.instalacao());
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
            Pageable pageable = PageRequest.of(0, 10);
            Clientes entity = new Clientes();
            Page<Clientes> page = new PageImpl<>(List.of(entity));
            PaginatedClientesResponseDTO responseDTO = new PaginatedClientesResponseDTO(0, 10, 1, 1, List.of(new ClienteResponseDTO(123L, "Teste", 0.0, 0.0)));

            when(clientesRepository.findAllByContaContrato(cc, pageable)).thenReturn(page);
            when(clientesMapper.entityToDtoPaginatedResponse(page)).thenReturn(responseDTO);

            assertNotNull(clientesService.findClienteByContaContrato(pageable, cc));
        }

        @Test
        @DisplayName("Should find by numero serie")
        void shouldFindByNumeroSerie() {
            Long ns = 789L;
            Pageable pageable = PageRequest.of(0, 10);
            Clientes entity = new Clientes();
            Page<Clientes> page = new PageImpl<>(List.of(entity));
            PaginatedClientesResponseDTO responseDTO = new PaginatedClientesResponseDTO(0, 10, 1, 1, List.of(new ClienteResponseDTO(123L, "Teste", 0.0, 0.0)));

            when(clientesRepository.findAllByNumeroSerie(ns, pageable)).thenReturn(page);
            when(clientesMapper.entityToDtoPaginatedResponse(page)).thenReturn(responseDTO);

            assertNotNull(clientesService.findClienteByNumeroSerie(pageable, ns));
        }

        @Test
        @DisplayName("Should find by numero poste")
        void shouldFindByNumeroPoste() {
            String np = "P123";
            Pageable pageable = PageRequest.of(0, 10);
            Clientes entity = new Clientes();
            Page<Clientes> page = new PageImpl<>(List.of(entity));
            PaginatedClientesResponseDTO responseDTO = new PaginatedClientesResponseDTO(0, 10, 1, 1, List.of(new ClienteResponseDTO(123L, "Teste", 0.0, 0.0)));

            when(clientesRepository.findAllByNumeroPoste(np, pageable)).thenReturn(page);
            when(clientesMapper.entityToDtoPaginatedResponse(page)).thenReturn(responseDTO);

            assertNotNull(clientesService.findClienteByNumeroPoste(pageable, np));
        }

        @Test
        @DisplayName("Should throw exception when conta contrato not found")
        void shouldThrowExceptionWhenContaContratoNotFound() {
            Long cc = 456L;
            Pageable pageable = PageRequest.of(0, 10);
            Page<Clientes> emptyPage = Page.empty();

            when(clientesRepository.findAllByContaContrato(cc, pageable)).thenReturn(emptyPage);

            assertThrows(EntityNotFoundException.class, () -> clientesService.findClienteByContaContrato(pageable, cc));
        }
    }
}