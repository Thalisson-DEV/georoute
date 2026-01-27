package com.sipel.backend.infra.database;

import com.sipel.backend.domain.Clientes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.util.List;

@Service
@Slf4j
public class ClientesBulkService {

    private final JdbcTemplate jdbcTemplate;

    public ClientesBulkService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void upsertClientes(List<Clientes> clientes) {
        String sql = """
            INSERT INTO clientes (instalacao, conta_contrato, numero_serie, numero_poste, nome_cliente, latitude, longitude)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (instalacao) DO UPDATE SET
                conta_contrato = EXCLUDED.conta_contrato,
                numero_serie = EXCLUDED.numero_serie,
                numero_poste = EXCLUDED.numero_poste,
                nome_cliente = EXCLUDED.nome_cliente,
                latitude = EXCLUDED.latitude,
                longitude = EXCLUDED.longitude
            WHERE
                clientes.conta_contrato IS DISTINCT FROM EXCLUDED.conta_contrato OR
                clientes.numero_serie IS DISTINCT FROM EXCLUDED.numero_serie OR
                clientes.numero_poste IS DISTINCT FROM EXCLUDED.numero_poste OR
                clientes.nome_cliente IS DISTINCT FROM EXCLUDED.nome_cliente OR
                clientes.latitude IS DISTINCT FROM EXCLUDED.latitude OR
                clientes.longitude IS DISTINCT FROM EXCLUDED.longitude
        """;

        int batchSize = 1000;

        jdbcTemplate.batchUpdate(sql, clientes, batchSize,
            (ps, cliente) -> {
                ps.setLong(1, cliente.getInstalacao());
                
                if (cliente.getContaContrato() != null) {
                    ps.setLong(2, cliente.getContaContrato());
                } else {
                    ps.setNull(2, Types.BIGINT);
                }

                if (cliente.getNumeroSerie() != null) {
                    ps.setLong(3, cliente.getNumeroSerie());
                } else {
                    ps.setNull(3, Types.BIGINT);
                }

                ps.setString(4, cliente.getNumeroPoste());
                ps.setString(5, cliente.getNomeCliente());

                if (cliente.getLatitude() != null) {
                    ps.setDouble(6, cliente.getLatitude());
                } else {
                    ps.setNull(6, Types.DOUBLE);
                }

                if (cliente.getLongitude() != null) {
                    ps.setDouble(7, cliente.getLongitude());
                } else {
                    ps.setNull(7, Types.DOUBLE);
                }
            });
    }
}
