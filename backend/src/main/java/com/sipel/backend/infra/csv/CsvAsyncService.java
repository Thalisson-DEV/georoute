package com.sipel.backend.infra.csv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.sipel.backend.domain.Clientes;
import com.sipel.backend.dtos.ClientesRequestDTO;
import com.sipel.backend.infra.database.ClientesBulkService;
import com.sipel.backend.mappers.ClientesMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CsvAsyncService {

    private final ClientesBulkService clientesBulkService;
    private final ClientesMapper clientesMapper;
    private final CacheManager cacheManager;
    private final Validator validator;

    public CsvAsyncService(ClientesBulkService clientesBulkService, ClientesMapper clientesMapper, CacheManager cacheManager, Validator validator) {
        this.clientesBulkService = clientesBulkService;
        this.clientesMapper = clientesMapper;
        this.cacheManager = cacheManager;
        this.validator = validator;
    }

    @Async
    public void processarCsvAsync(File file) {
        log.info("Iniciando processamento do arquivo CSV: {}", file.getName());

        try (Reader reader = new BufferedReader(new FileReader(file))) {
            CsvToBean<ClientesRequestDTO> csvToBean = new CsvToBeanBuilder<ClientesRequestDTO>(reader)
                    .withType(ClientesRequestDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<ClientesRequestDTO> clientesDtos = csvToBean.parse();
            log.info("CSV lido com sucesso. Convertendo {} registros...", clientesDtos.size());

            List<Clientes> clientesEntities = clientesDtos.stream()
                    .map(dto -> {
                        Set<ConstraintViolation<ClientesRequestDTO>> violations = validator.validate(dto);
                        if (!violations.isEmpty()) {
                            String errorMsg = violations.stream()
                                    .map(ConstraintViolation::getMessage)
                                    .collect(Collectors.joining("; "));
                            throw new IllegalArgumentException("Erro de validação no registro (Instalação: " + dto.getInstalacao() + "): " + errorMsg);
                        }
                        return clientesMapper.dtoRequestToEntity(dto);
                    })
                    .toList();

            clientesBulkService.upsertClientes(clientesEntities);

            evictAllCacheValues();
            
            log.info("Processamento finalizado com sucesso.");

        } catch (IOException e) {
            log.error("Erro fatal no processamento", e);
        } finally {
            if (file.exists() && !file.delete()) {
                log.warn("Falha ao remover arquivo temporário: {}", file.getName());
            } else {
                log.info("Arquivo temporário removido.");
            }
        }
    }

    private void evictAllCacheValues() {
        if (cacheManager.getCache("cliente") != null) {
            Objects.requireNonNull(cacheManager.getCache("cliente")).clear();
            log.info("Cache '{}' invalidado com sucesso.", "cliente");
        }
    }
}
