package com.sipel.backend.repositories;

import com.sipel.backend.domain.ExecucoesRota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface ExecucoesRotaRepository extends JpaRepository<ExecucoesRota, String> {
    Optional<ExecucoesRota> findByEquipeIdAndData(Long equipeId, LocalDate data);
}
