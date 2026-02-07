package com.sipel.backend.controllers;

import com.sipel.backend.domain.enums.SetorEnum;
import com.sipel.backend.dtos.EquipesRequestDTO;
import com.sipel.backend.dtos.EquipesResponseDTO;
import com.sipel.backend.services.EquipesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.sipel.backend.domain.enums.MunicipioEnum;

@RestController
@RequestMapping("/api/v1/equipes")
@Tag(name = "Equipes", description = "Gerenciamento de Equipes")
public class EquipesController {

    private final EquipesService equipesService;

    public EquipesController(EquipesService equipesService) {
        this.equipesService = equipesService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar Equipe", description = "Cria uma nova equipe com localização base e setor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Equipe criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou equipe já existente")
    })
    public ResponseEntity<Void> saveEquipe(@RequestBody @Valid EquipesRequestDTO request) {
        equipesService.saveEquipe(request);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar Equipe", description = "Atualiza os dados de uma equipe existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipe atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Equipe não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou conflito de nome")
    })
    public ResponseEntity<Void> updateEquipe(
            @Parameter(description = "ID da equipe", example = "1")
            @PathVariable Long id,
            @RequestBody @Valid EquipesRequestDTO request) {
        equipesService.updateEquipe(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover Equipe", description = "Remove uma equipe do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Equipe removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Equipe não encontrada")
    })
    public ResponseEntity<Void> deleteEquipe(
            @Parameter(description = "ID da equipe", example = "1")
            @PathVariable Long id) {
        equipesService.deleteEquipe(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Listar Equipes", description = "Retorna lista de equipes, opcionalmente filtrada por setor e/ou município.")
    public ResponseEntity<List<EquipesResponseDTO>> findAllEquipes(
            @Parameter(description = "Setor para filtro (COMERCIAL ou LEITURA)")
            @RequestParam(required = false) SetorEnum setor,
            @Parameter(description = "Município para filtro (JUAZEIRO, REMANSO, BONFIM, JACOBINA)")
            @RequestParam(required = false) MunicipioEnum municipio) {
        return ResponseEntity.ok(equipesService.findAllEquipes(setor, municipio));
    }
}