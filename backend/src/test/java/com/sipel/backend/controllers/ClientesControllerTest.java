package com.sipel.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipel.backend.dtos.ClientesRequestDTO;
import com.sipel.backend.dtos.ClientesResponseDTO;
import com.sipel.backend.infra.csv.CsvImportService;
import com.sipel.backend.services.ClientesService;
import com.sipel.backend.services.TokenService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

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
    private TokenService tokenService; // Needed because SecurityFilter might depend on it

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
        ClientesResponseDTO response = new ClientesResponseDTO(id, 456L, 789L, "P123", "Nome Teste", -23.1, -46.1);

        when(clientesService.findClienteByInstalacao(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/clientes/instalacao/{instalacao}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instalacao").value(id))
                .andExpect(jsonPath("$.nome").value("Nome Teste"));
    }

    @Test
    @DisplayName("Should return cliente by conta contrato")
    void shouldReturnClienteByContaContrato() throws Exception {
        Long cc = 456L;
        ClientesResponseDTO response = new ClientesResponseDTO(123L, cc, 789L, "P123", "Nome Teste", -23.1, -46.1);

        when(clientesService.findClienteByContaContrato(cc)).thenReturn(response);

        mockMvc.perform(get("/api/v1/clientes/conta-contrato/{contaContrato}", cc))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contaContrato").value(cc));
    }
}
