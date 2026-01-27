package com.sipel.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity(name = "clientes")
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "instalacao")
public class Clientes {

    @Id
    @Column(unique = true)
    private Long instalacao;

    private Long contaContrato;
    private Long numeroSerie;
    private String numeroPoste;
    private String nomeCliente;

    private Double latitude;
    private Double longitude;
}
