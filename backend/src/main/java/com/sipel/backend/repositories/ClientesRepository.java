package com.sipel.backend.repositories;

import com.sipel.backend.domain.Clientes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientesRepository extends JpaRepository<Clientes, Long> {

    Optional<Clientes> findByContaContrato(Long contaContrato);

    Optional<Clientes> findByNumeroSerie(Long numeroSerie);

    Optional<Clientes> findByNumeroPoste(String numeroPoste);

    Page<Clientes> findAllByContaContrato(Long contaContrato, Pageable pageable);

    Page<Clientes> findAllByNumeroSerie(Long numeroSerie, Pageable pageable);

    Page<Clientes> findAllByNumeroPoste(String numeroPoste, Pageable pageable);
}
