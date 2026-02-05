package com.sipel.backend.repositories;

import com.sipel.backend.domain.Equipes;
import com.sipel.backend.domain.enums.SetorEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EquipesRepository extends JpaRepository<Equipes, Long> {
    List<Equipes> findAllBySetor(SetorEnum setor);
    boolean existsByNome(String nome);
}