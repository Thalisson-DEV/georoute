package com.sipel.backend.repositories;

import com.sipel.backend.domain.Equipes;
import com.sipel.backend.domain.enums.SetorEnum;
import com.sipel.backend.domain.enums.MunicipioEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EquipesRepository extends JpaRepository<Equipes, Long> {
    List<Equipes> findAllBySetor(SetorEnum setor);
    List<Equipes> findAllByMunicipio(MunicipioEnum municipio);
    List<Equipes> findAllBySetorAndMunicipio(SetorEnum setor, MunicipioEnum municipio);
    boolean existsByNome(String nome);
}