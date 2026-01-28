package com.sipel.backend.dtos;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientesRequestDTO {

    @CsvBindByName(column = "instalacao")
    @NotNull(message = "O numero da instalação não deve estar em branco.")
    private Long instalacao;

    @CsvBindByName(column = "conta_contrato")
    @NotNull(message = "A conta contrato não deve estar em branco.")
    private Long contaContrato;

    @CsvBindByName(column = "numero_serie")
    @NotNull(message = "O numero de serie não deve estar em branco.")
    private Long numeroSerie;

    @CsvBindByName(column = "numero_poste")
    private String numeroPoste;

    @CsvBindByName(column = "nome_cliente")
    private String nomeCliente;

    @CsvBindByName(column = "latitude")
    private Double latitude;

    @CsvBindByName(column = "longitude")
    private Double longitude;
}

