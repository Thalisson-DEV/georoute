package com.sipel.backend.domain;

import com.sipel.backend.domain.enums.SetorEnum;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "equipes")
@Entity(name = "equipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Equipes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nome;
    
    @Column(name = "latitude_base")
    private Double latitudeBase;
    
    @Column(name = "longitude_base")
    private Double longitudeBase;

    @Enumerated(EnumType.STRING)
    private SetorEnum setor;
}