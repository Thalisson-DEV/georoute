package com.sipel.backend.services;

import com.sipel.backend.domain.Equipes;
import com.sipel.backend.domain.enums.SetorEnum;
import com.sipel.backend.dtos.EquipesRequestDTO;
import com.sipel.backend.dtos.EquipesResponseDTO;
import com.sipel.backend.exceptions.EntityAlreadyExistsException;
import com.sipel.backend.mappers.EquipesMapper;
import com.sipel.backend.repositories.EquipesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipesServiceTest {

    @Mock
    private EquipesRepository equipesRepository;

    @Mock
    private EquipesMapper equipesMapper;

    @InjectMocks
    private EquipesService equipesService;

    @Test
    @DisplayName("Should save equipe successfully when name does not exist")
    void shouldSaveEquipe() {
        EquipesRequestDTO request = new EquipesRequestDTO("Equipe Alpha", -23.0, -46.0, SetorEnum.LEITURA);
        Equipes entity = new Equipes(1L, "Equipe Alpha", -23.0, -46.0, SetorEnum.LEITURA);

        when(equipesRepository.existsByNome(request.nome())).thenReturn(false);
        when(equipesMapper.dtoRequestToEntity(request)).thenReturn(entity);
        when(equipesRepository.save(entity)).thenReturn(entity);

        assertDoesNotThrow(() -> equipesService.saveEquipe(request));
        verify(equipesRepository).save(entity);
    }

    @Test
    @DisplayName("Should throw EntityAlreadyExistsException when name already exists during save")
    void shouldThrowExceptionWhenNameExistsOnSave() {
        EquipesRequestDTO request = new EquipesRequestDTO("Equipe Alpha", -23.0, -46.0, SetorEnum.LEITURA);

        when(equipesRepository.existsByNome(request.nome())).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> equipesService.saveEquipe(request));
        verify(equipesRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update equipe successfully")
    void shouldUpdateEquipe() {
        Long id = 1L;
        EquipesRequestDTO request = new EquipesRequestDTO("Equipe Beta", -23.1, -46.1, SetorEnum.COMERCIAL);
        Equipes existing = new Equipes(id, "Equipe Alpha", -23.0, -46.0, SetorEnum.LEITURA);

        when(equipesRepository.findById(id)).thenReturn(Optional.of(existing));
        when(equipesRepository.existsByNome(request.nome())).thenReturn(false);
        when(equipesRepository.save(existing)).thenReturn(existing);

        assertDoesNotThrow(() -> equipesService.updateEquipe(id, request));
        
        verify(equipesRepository).save(existing);
        assertEquals("Equipe Beta", existing.getNome());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating non-existent equipe")
    void shouldThrowExceptionWhenUpdatingNonExistent() {
        Long id = 1L;
        EquipesRequestDTO request = new EquipesRequestDTO("Equipe Beta", -23.1, -46.1, SetorEnum.COMERCIAL);

        when(equipesRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> equipesService.updateEquipe(id, request));
    }

    @Test
    @DisplayName("Should delete equipe successfully")
    void shouldDeleteEquipe() {
        Long id = 1L;
        when(equipesRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> equipesService.deleteEquipe(id));
        verify(equipesRepository).deleteById(id);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent equipe")
    void shouldThrowExceptionWhenDeletingNonExistent() {
        Long id = 1L;
        when(equipesRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> equipesService.deleteEquipe(id));
    }

    @Test
    @DisplayName("Should list all equipes filtered by setor")
    void shouldListEquipesBySetor() {
        SetorEnum setor = SetorEnum.COMERCIAL;
        List<Equipes> entities = List.of(new Equipes());
        List<EquipesResponseDTO> responses = List.of(new EquipesResponseDTO(1L, "A", 0.0, 0.0, setor));

        when(equipesRepository.findAllBySetor(setor)).thenReturn(entities);
        when(equipesMapper.entityListToDtoResponseList(entities)).thenReturn(responses);

        List<EquipesResponseDTO> result = equipesService.findAllEquipes(setor);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(equipesRepository).findAllBySetor(setor);
    }
}