package com.sipel.backend.repositories;

import com.sipel.backend.domain.Clientes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientesRepository extends JpaRepository<Clientes, Long> {

    Optional<Clientes> findByContaContrato(Long contaContrato);

    Optional<Clientes> findByNumeroSerie(Long numeroSerie);

    Optional<Clientes> findByNumeroPoste(String numeroPoste);
}
