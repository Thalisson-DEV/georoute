package com.sipel.backend.services;

import com.sipel.backend.domain.Clientes;
import com.sipel.backend.infra.database.ClientesBulkService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClientesBulkServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ClientesBulkService clientesBulkService;

    @Test
    void testUpsertClientes() {
        // Arrange
        Clientes c1 = new Clientes(1L, 100L, 200L, "P1", "John", -10.0, -20.0);
        Clientes c2 = new Clientes(2L, 101L, 201L, "P2", "Jane", -11.0, -21.0);
        List<Clientes> clientes = Arrays.asList(c1, c2);

        // Act
        clientesBulkService.upsertClientes(clientes);

        // Assert
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).batchUpdate(
                sqlCaptor.capture(),
                eq(clientes),
                eq(1000),
                any(ParameterizedPreparedStatementSetter.class)
        );

        String capturedSql = sqlCaptor.getValue();
        assertTrue(capturedSql.contains("INSERT INTO clientes"));
        assertTrue(capturedSql.contains("ON CONFLICT (instalacao) DO UPDATE SET"));
        assertTrue(capturedSql.contains("conta_contrato = EXCLUDED.conta_contrato"));
    }
}
