package com.sipel.backend.repositories;

import com.sipel.backend.domain.Clientes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class ClientesRepositoryTest {

    @Autowired
    private ClientesRepository clientesRepository;

    @Test
    @DisplayName("Should find cliente by conta contrato")
    void shouldFindByContaContrato() {
        Clientes cliente = new Clientes(123L, 456L, 789L, "P123", "Teste", -23.0, -46.0);
        clientesRepository.save(cliente);

        Optional<Clientes> found = clientesRepository.findByContaContrato(456L);

        assertTrue(found.isPresent());
        assertEquals(123L, found.get().getInstalacao());
    }

    @Test
    @DisplayName("Should find cliente by numero serie")
    void shouldFindByNumeroSerie() {
        Clientes cliente = new Clientes(123L, 456L, 789L, "P123", "Teste", -23.0, -46.0);
        clientesRepository.save(cliente);

        Optional<Clientes> found = clientesRepository.findByNumeroSerie(789L);

        assertTrue(found.isPresent());
        assertEquals("Teste", found.get().getNomeCliente());
    }

    @Test
    @DisplayName("Should find cliente by numero poste")
    void shouldFindByNumeroPoste() {
        Clientes cliente = new Clientes(123L, 456L, 789L, "P123", "Teste", -23.0, -46.0);
        clientesRepository.save(cliente);

        Optional<Clientes> found = clientesRepository.findByNumeroPoste("P123");

        assertTrue(found.isPresent());
        assertEquals(123L, found.get().getInstalacao());
    }
}
