package com.sipel.backend.mappers;

import com.sipel.backend.domain.Equipes;
import com.sipel.backend.dtos.EquipesRequestDTO;
import com.sipel.backend.dtos.EquipesResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EquipesMapper {
    Equipes dtoRequestToEntity(EquipesRequestDTO dto);
    EquipesResponseDTO entityToDtoResponse(Equipes entity);
    List<EquipesResponseDTO> entityListToDtoResponseList(List<Equipes> entities);
}