package com.sipel.backend.controllers;

import com.sipel.backend.dtos.ClientesRequestDTO;
import com.sipel.backend.dtos.ClientesResponseDTO;
import com.sipel.backend.infra.csv.CsvImportService;
import com.sipel.backend.services.ClientesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/clientes")
public class ClientesController {

    private final ClientesService clientesService;
    private final CsvImportService csvImportService;

    public ClientesController(ClientesService clientesService, CsvImportService csvImportService) {
        this.clientesService = clientesService;
        this.csvImportService = csvImportService;
    }

    @PostMapping(value = "/import")
    public ResponseEntity<String> importClientes(@RequestParam("file") MultipartFile file) throws IOException {
        csvImportService.InitializeImport(file);
        return ResponseEntity.accepted().body("Importação iniciada em segundo plano.");
    }

    @PostMapping()
    public ResponseEntity<Void> saveCliente(@RequestBody ClientesRequestDTO clientesRequestDTO) {
        clientesService.saveCliente(clientesRequestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("instalacao/{instalacao}")
    public ResponseEntity<ClientesResponseDTO> findClienteByInstalacao(@PathVariable Long instalacao) {
        return ResponseEntity.ok(clientesService.findClienteByInstalacao(instalacao));
    }

    @GetMapping("conta-contrato/{contaContrato}")
    public ResponseEntity<ClientesResponseDTO> findClienteByContaContrato(@PathVariable Long contaContrato) {
        return ResponseEntity.ok(clientesService.findClienteByContaContrato(contaContrato));
    }

    @GetMapping("numero-serie/{numeroSerie}")
    public ResponseEntity<ClientesResponseDTO> findClienteByNumeroSerie(@PathVariable Long numeroSerie) {
        return ResponseEntity.ok(clientesService.findClienteByNumeroSerie(numeroSerie));
    }

    @GetMapping("numero-poste/{numeroPoste}")
    public ResponseEntity<ClientesResponseDTO> findClienteByNumeroPoste(@PathVariable String numeroPoste) {
        return ResponseEntity.ok(clientesService.findClienteByNumeroPoste(numeroPoste));
    }

}
