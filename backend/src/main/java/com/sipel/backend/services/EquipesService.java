package com.sipel.backend.services;

import com.sipel.backend.domain.Equipes;
import com.sipel.backend.domain.enums.SetorEnum;
import com.sipel.backend.dtos.EquipesRequestDTO;
import com.sipel.backend.dtos.EquipesResponseDTO;
import com.sipel.backend.exceptions.EntityAlreadyExistsException;
import com.sipel.backend.mappers.EquipesMapper;
import com.sipel.backend.repositories.EquipesRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import java.util.List;

import com.sipel.backend.domain.enums.MunicipioEnum;

@Service
@RequiredArgsConstructor
@Validated
public class EquipesService {

    private final EquipesRepository equipesRepository;
    private final EquipesMapper equipesMapper;

    @Transactional
    @CacheEvict(value = "equipes", allEntries = true)
    public void saveEquipe(@Valid EquipesRequestDTO request) {
        if (equipesRepository.existsByNome(request.nome())) {
            throw new EntityAlreadyExistsException("Já existe uma equipe cadastrada com o nome: " + request.nome());
        }

        Equipes equipe = equipesMapper.dtoRequestToEntity(request);
        equipesRepository.save(equipe);
    }

    @Transactional
    @CacheEvict(value = "equipes", allEntries = true)
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
        equipe.setMunicipio(request.municipio());

        equipesRepository.save(equipe);
    }

    @Transactional
    @CacheEvict(value = "equipes", allEntries = true)
    public void deleteEquipe(Long id) {
        if (!equipesRepository.existsById(id)) {
            throw new EntityNotFoundException("Equipe não encontrada com ID: " + id);
        }
        equipesRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "equipes", key = "T(java.lang.String).format('%s-%s', #setor, #municipio)")
    public List<EquipesResponseDTO> findAllEquipes(SetorEnum setor, MunicipioEnum municipio) {
        List<Equipes> equipes;
        if (setor != null && municipio != null) {
            equipes = equipesRepository.findAllBySetorAndMunicipio(setor, municipio);
        } else if (setor != null) {
            equipes = equipesRepository.findAllBySetor(setor);
        } else if (municipio != null) {
            equipes = equipesRepository.findAllByMunicipio(municipio);
        } else {
            equipes = equipesRepository.findAll();
        }
        return equipesMapper.entityListToDtoResponseList(equipes);
    }
}
