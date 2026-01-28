package com.sipel.backend.controllers;

import com.sipel.backend.dtos.ClientesRequestDTO;
import com.sipel.backend.dtos.ClientesResponseDTO;
import com.sipel.backend.infra.csv.CsvImportService;
import com.sipel.backend.services.ClientesService;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/clientes")
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes e importação de dados")
public class ClientesController {

    private final ClientesService clientesService;
    private final CsvImportService csvImportService;
    private final MeterRegistry meterRegistry;

    public ClientesController(ClientesService clientesService, CsvImportService csvImportService, MeterRegistry meterRegistry) {
        this.clientesService = clientesService;
        this.csvImportService = csvImportService;
        this.meterRegistry = meterRegistry;
    }

    @Operation(summary = "Importar Clientes via CSV", description = "Recebe um arquivo CSV e inicia o processamento de importação em segundo plano.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Importação iniciada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar o arquivo")
    })
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importClientes(
            @Parameter(description = "Arquivo CSV contendo dados dos clientes")
            @RequestParam("file") MultipartFile file) throws IOException {
        csvImportService.InitializeImport(file);
        return ResponseEntity.accepted().body("Importação iniciada em segundo plano.");
    }

    @Operation(summary = "Cadastrar Cliente", description = "Cria um novo registro de cliente manualmente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping()
    public ResponseEntity<Void> saveCliente(@RequestBody ClientesRequestDTO clientesRequestDTO) {
        clientesService.saveCliente(clientesRequestDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Buscar por Instalação", description = "Retorna os dados do cliente baseado no número da instalação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("instalacao/{instalacao}")
    public ResponseEntity<ClientesResponseDTO> findClienteByInstalacao(
            @Parameter(description = "Número da instalação", example = "12345678")
            @PathVariable Long instalacao) {
        meterRegistry.counter("business.clientes.consultas", "tipo", "instalacao").increment();
        return ResponseEntity.ok(clientesService.findClienteByInstalacao(instalacao));
    }

    @Operation(summary = "Buscar por Conta Contrato", description = "Retorna os dados do cliente baseado na conta contrato.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("conta-contrato/{contaContrato}")
    public ResponseEntity<ClientesResponseDTO> findClienteByContaContrato(
            @Parameter(description = "Número da conta contrato", example = "7000123456")
            @PathVariable Long contaContrato) {
        meterRegistry.counter("business.clientes.consultas", "tipo", "conta_contrato").increment();
        return ResponseEntity.ok(clientesService.findClienteByContaContrato(contaContrato));
    }

    @Operation(summary = "Buscar por Número de Série", description = "Retorna os dados do cliente baseado no número de série do medidor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("numero-serie/{numeroSerie}")
    public ResponseEntity<ClientesResponseDTO> findClienteByNumeroSerie(
            @Parameter(description = "Número de série do medidor", example = "987654321")
            @PathVariable Long numeroSerie) {
        meterRegistry.counter("business.clientes.consultas", "tipo", "numero_serie").increment();
        return ResponseEntity.ok(clientesService.findClienteByNumeroSerie(numeroSerie));
    }

    @Operation(summary = "Buscar por Número do Poste", description = "Retorna os dados do cliente baseado no número do poste.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("numero-poste/{numeroPoste}")
    public ResponseEntity<ClientesResponseDTO> findClienteByNumeroPoste(
            @Parameter(description = "Identificação do poste", example = "P-12345")
            @PathVariable String numeroPoste) {
        meterRegistry.counter("business.clientes.consultas", "tipo", "numero_poste").increment();
        return ResponseEntity.ok(clientesService.findClienteByNumeroPoste(numeroPoste));
    }

}
