package com.sipel.backend.services;

import com.sipel.backend.domain.Clientes;
import com.sipel.backend.dtos.ClientesRequestDTO;
import com.sipel.backend.dtos.ClientesResponseDTO;
import com.sipel.backend.exceptions.EntityAlreadyExistsException;
import com.sipel.backend.mappers.ClientesMapper;
import com.sipel.backend.repositories.ClientesRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
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
    public ClientesResponseDTO findClienteByInstalacao(@Valid Long instalacao) {
        Clientes clientes = clientesRepository.findById(instalacao)
                .orElseThrow(() -> new EntityNotFoundException("Cliente inexistente com o id: " + instalacao));

        return clientesMapper.entityToDtoResponse(clientes);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "cliente", key = "#contaContrato")
    public ClientesResponseDTO findClienteByContaContrato(@Valid Long contaContrato) {
        Clientes clientes = clientesRepository.findByContaContrato(contaContrato)
                .orElseThrow(() -> new EntityNotFoundException("Cliente inexistente com o id: " + contaContrato));

        return clientesMapper.entityToDtoResponse(clientes);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "cliente", key = "#numeroSerie")
    public ClientesResponseDTO findClienteByNumeroSerie(@Valid Long numeroSerie) {
        Clientes clientes = clientesRepository.findByNumeroSerie(numeroSerie)
                .orElseThrow(() -> new EntityNotFoundException("Cliente inexistente com o id: " + numeroSerie));

        return clientesMapper.entityToDtoResponse(clientes);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "cliente", key = "#numeroPoste")
    public ClientesResponseDTO findClienteByNumeroPoste(@Valid String numeroPoste) {
        Clientes clientes = clientesRepository.findByNumeroPoste(numeroPoste)
                .orElseThrow(() -> new EntityNotFoundException("Cliente inexistente com o id: " + numeroPoste));

        return clientesMapper.entityToDtoResponse(clientes);
    }
}
