package com.sipel.backend.services;

import com.sipel.backend.domain.Clientes;
import com.sipel.backend.dtos.ClienteResponseDTO;
import com.sipel.backend.dtos.ClientesRequestDTO;
import com.sipel.backend.dtos.PaginatedClientesResponseDTO;
import com.sipel.backend.exceptions.EntityAlreadyExistsException;
import com.sipel.backend.mappers.ClientesMapper;
import com.sipel.backend.repositories.ClientesRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientesService {

    private final ClientesRepository clientesRepository;
    private final ClientesMapper clientesMapper;

    public ClientesService(ClientesRepository clientesRepository, ClientesMapper clientesMapper) {
        this.clientesRepository = clientesRepository;
        this.clientesMapper = clientesMapper;
    }

    @Transactional
    public void saveCliente(@Valid ClientesRequestDTO clientesRequestDTO) {
        if (clientesRepository.existsById(clientesRequestDTO.getInstalacao())) {
            throw new EntityAlreadyExistsException("Cliente já existente com o id: " + clientesRequestDTO.getInstalacao());
        }

        Clientes clientes = clientesMapper.dtoRequestToEntity(clientesRequestDTO);

        clientesRepository.save(clientes);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "cliente", key = "#instalacao")
    public ClienteResponseDTO findClienteByInstalacao(@Valid Long instalacao) {
        Clientes clientes = clientesRepository.findById(instalacao)
                .orElseThrow(() -> new EntityNotFoundException("Cliente inexistente com o id: " + instalacao));

        return clientesMapper.entityToDtoResponse(clientes);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "cliente", key = "#contaContrato")
    public PaginatedClientesResponseDTO findClienteByContaContrato(Pageable pageable, Long contaContrato) {
        Page<Clientes> clientes = this.clientesRepository.findAllByContaContrato(contaContrato, pageable);

        if (clientes.isEmpty()) {
            throw new EntityNotFoundException("Cliente inexistente com o id: " + contaContrato);
        }

        return clientesMapper.entityToDtoPaginatedResponse(clientes);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "cliente", key = "#numeroSerie")
    public PaginatedClientesResponseDTO findClienteByNumeroSerie(Pageable pageable, Long numeroSerie) {
        Page<Clientes> clientes = this.clientesRepository.findAllByNumeroSerie(numeroSerie, pageable);

        if (clientes.isEmpty()) {
            throw new EntityNotFoundException("Cliente inexistente com o id: " + numeroSerie);
        }

        return clientesMapper.entityToDtoPaginatedResponse(clientes);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "cliente", key = "#numeroPoste")
    public PaginatedClientesResponseDTO findClienteByNumeroPoste(Pageable pageable, String numeroPoste) {
        Page<Clientes> clientes = this.clientesRepository.findAllByNumeroPoste(numeroPoste, pageable);

        if (clientes.isEmpty()) {
            throw new EntityNotFoundException("Cliente inexistente com o id: " + numeroPoste);
        }

        return clientesMapper.entityToDtoPaginatedResponse(clientes);
    }
}
