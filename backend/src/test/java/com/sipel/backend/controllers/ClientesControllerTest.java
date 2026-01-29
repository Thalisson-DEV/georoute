package com.sipel.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipel.backend.dtos.ClienteResponseDTO;
import com.sipel.backend.dtos.ClientesRequestDTO;
import com.sipel.backend.dtos.PaginatedClientesResponseDTO;
import com.sipel.backend.infra.csv.CsvImportService;
import com.sipel.backend.services.ClientesService;
import com.sipel.backend.services.TokenService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientesController.class)
@AutoConfigureMockMvc(addFilters = false) // Disabling security filters for simplicity
class ClientesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientesService clientesService;

    @MockBean
    private CsvImportService csvImportService;

    @MockBean
    private MeterRegistry meterRegistry;

    @MockBean
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        Counter mockCounter = mock(Counter.class);
        when(meterRegistry.counter(anyString(), any(String[].class))).thenReturn(mockCounter);
    }

    @Test
    @DisplayName("Should return 202 when importing CSV")
    void shouldReturnAcceptedWhenImportingCsv() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());

        mockMvc.perform(multipart("/api/v1/clientes/import").file(file))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Importação iniciada em segundo plano."));
    }

    @Test
    @DisplayName("Should return 200 when saving cliente")
    void shouldReturnOkWhenSavingCliente() throws Exception {
        ClientesRequestDTO request = new ClientesRequestDTO(123L, 456L, 789L, "P123", "Nome Teste", -23.1, -46.1);

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return cliente by instalacao")
    void shouldReturnClienteByInstalacao() throws Exception {
        Long id = 123L;
        ClienteResponseDTO response = new ClienteResponseDTO(id, "Nome Teste", -23.1, -46.1);

        when(clientesService.findClienteByInstalacao(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/clientes/instalacao/{instalacao}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instalacao").value(id))
                .andExpect(jsonPath("$.nomeCliente").value("Nome Teste"));
    }

    @Test
    @DisplayName("Should return paginated clientes by conta contrato")
    void shouldReturnClienteByContaContrato() throws Exception {
        Long cc = 456L;
        ClienteResponseDTO client = new ClienteResponseDTO(123L, "Nome Teste", -23.1, -46.1);
        PaginatedClientesResponseDTO response = new PaginatedClientesResponseDTO(
                0, 10, 1, 1, List.of(client)
        );

        when(clientesService.findClienteByContaContrato(any(Pageable.class), eq(cc))).thenReturn(response);

        mockMvc.perform(get("/api/v1/clientes/conta-contrato/{contaContrato}", cc)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.data[0].instalacao").value(123L));
    }

    @Test
    @DisplayName("Should return paginated clientes by numero serie")
    void shouldReturnClienteByNumeroSerie() throws Exception {
        Long ns = 789L;
        ClienteResponseDTO client = new ClienteResponseDTO(123L, "Nome Teste", -23.1, -46.1);
        PaginatedClientesResponseDTO response = new PaginatedClientesResponseDTO(
                0, 10, 1, 1, List.of(client)
        );

        when(clientesService.findClienteByNumeroSerie(any(Pageable.class), eq(ns))).thenReturn(response);

        mockMvc.perform(get("/api/v1/clientes/numero-serie/{numeroSerie}", ns)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.data[0].instalacao").value(123L));
    }

    @Test
    @DisplayName("Should return paginated clientes by numero poste")
    void shouldReturnClienteByNumeroPoste() throws Exception {
        String poste = "P123";
        ClienteResponseDTO client = new ClienteResponseDTO(123L, "Nome Teste", -23.1, -46.1);
        PaginatedClientesResponseDTO response = new PaginatedClientesResponseDTO(
                0, 10, 1, 1, List.of(client)
        );

        when(clientesService.findClienteByNumeroPoste(any(Pageable.class), eq(poste))).thenReturn(response);

        mockMvc.perform(get("/api/v1/clientes/numero-poste/{numeroPoste}", poste)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.data[0].instalacao").value(123L));
    }
}