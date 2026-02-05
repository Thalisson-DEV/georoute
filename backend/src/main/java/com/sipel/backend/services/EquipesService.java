package com.sipel.backend.services;

import com.sipel.backend.domain.Equipes;
import com.sipel.backend.domain.enums.SetorEnum;
import com.sipel.backend.dtos.EquipesRequestDTO;
import com.sipel.backend.dtos.EquipesResponseDTO;
import com.sipel.backend.exceptions.EntityAlreadyExistsException;
import com.sipel.backend.mappers.EquipesMapper;
import com.sipel.backend.repositories.EquipesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import java.util.List;

@Service
@Validated
public class EquipesService {

    private final EquipesRepository equipesRepository;
    private final EquipesMapper equipesMapper;

    public EquipesService(EquipesRepository equipesRepository, EquipesMapper equipesMapper) {
        this.equipesRepository = equipesRepository;
        this.equipesMapper = equipesMapper;
    }

    @Transactional
    public void saveEquipe(@Valid EquipesRequestDTO request) {
        if (equipesRepository.existsByNome(request.nome())) {
            throw new EntityAlreadyExistsException("Já existe uma equipe cadastrada com o nome: " + request.nome());
        }

        Equipes equipe = equipesMapper.dtoRequestToEntity(request);
        equipesRepository.save(equipe);
    }

    @Transactional
    public void updateEquipe(Long id, @Valid EquipesRequestDTO request) {
        Equipes equipe = equipesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada com ID: " + id));

        if (!equipe.getNome().equals(request.nome()) && equipesRepository.existsByNome(request.nome())) {
            throw new EntityAlreadyExistsException("Já existe uma equipe com o nome: " + request.nome());
        }

        equipe.setNome(request.nome());
        equipe.setLatitudeBase(request.latitudeBase());
        equipe.setLongitudeBase(request.longitudeBase());
        equipe.setSetor(request.setor());

        equipesRepository.save(equipe);
    }

    @Transactional
    public void deleteEquipe(Long id) {
        if (!equipesRepository.existsById(id)) {
            throw new EntityNotFoundException("Equipe não encontrada com ID: " + id);
        }
        equipesRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EquipesResponseDTO> findAllEquipes(SetorEnum setor) {
        List<Equipes> equipes;
        if (setor != null) {
            equipes = equipesRepository.findAllBySetor(setor);
        } else {
            equipes = equipesRepository.findAll();
        }
        return equipesMapper.entityListToDtoResponseList(equipes);
    }
}