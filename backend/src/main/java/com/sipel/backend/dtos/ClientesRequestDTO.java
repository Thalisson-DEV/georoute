package com.sipel.backend.dtos;

import com.opencsv.bean.CsvBindByName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação ou atualização de um cliente")
public class ClientesRequestDTO {

    @CsvBindByName(column = "instalacao")
    @NotNull(message = "O numero da instalação não deve estar em branco.")
    @Schema(description = "Número da instalação do cliente", example = "12345678")
    private Long instalacao;

    @CsvBindByName(column = "conta_contrato")
    @NotNull(message = "A conta contrato não deve estar em branco.")
    @Schema(description = "Número da conta contrato", example = "7000123456")
    private Long contaContrato;

    @CsvBindByName(column = "numero_serie")
    @NotNull(message = "O numero de serie não deve estar em branco.")
    @Schema(description = "Número de série do medidor", example = "987654321")
    private Long numeroSerie;

    @CsvBindByName(column = "numero_poste")
    @Schema(description = "Identificação do poste de energia", example = "P-12345")
    private String numeroPoste;

    @CsvBindByName(column = "nome_cliente")
    @Schema(description = "Nome completo do cliente", example = "João da Silva")
    private String nomeCliente;

    @CsvBindByName(column = "latitude")
    @Schema(description = "Latitude da localização", example = "-23.550520")
    private Double latitude;

    @CsvBindByName(column = "longitude")
    @Schema(description = "Longitude da localização", example = "-46.633308")
    private Double longitude;
}

