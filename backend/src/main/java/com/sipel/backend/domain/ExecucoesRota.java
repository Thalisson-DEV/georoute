package com.sipel.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Table(name = "execucoes_rota")
@Entity(name = "execucoes_rota")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ExecucoesRota {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "equipe_id")
    private Long equipeId;

    private LocalDate data;

    @Column(columnDefinition = "TEXT", name = "rota_json")
    private String rotaJson;
}
