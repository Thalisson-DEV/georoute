package com.sipel.backend.mappers;

import com.sipel.backend.domain.ExecucoesRota;
import com.sipel.backend.dtos.RouteHistoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RouteHistoryMapper {
    
    @Mapping(target = "createdAt", expression = "java(entity.getData().toString())")
    RouteHistoryDTO toDTO(ExecucoesRota entity);

    List<RouteHistoryDTO> toDTOList(List<ExecucoesRota> entities);
}
