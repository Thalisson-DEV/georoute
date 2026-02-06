package com.sipel.backend.repositories;

import com.sipel.backend.domain.ExecucoesRota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExecucoesRotaRepository extends JpaRepository<ExecucoesRota, String> {
    Optional<ExecucoesRota> findByEquipeIdAndData(Long equipeId, LocalDate data);
    
    List<ExecucoesRota> findAllByEquipeIdOrderByDataDesc(Long equipeId);

    @Modifying
    @Query("DELETE FROM execucoes_rota e WHERE e.data < :cutoffDate")
    void deleteOlderThan(@Param("cutoffDate") LocalDate cutoffDate);
}