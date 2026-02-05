package com.sipel.backend.mappers;

import com.sipel.backend.dtos.ClientDTO;
import com.sipel.backend.dtos.ors.OrsJobDTO;
import com.sipel.backend.dtos.ors.OrsOptimizationRequestDTO;
import com.sipel.backend.dtos.ors.OrsVehicleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RouteMapper {

    default OrsOptimizationRequestDTO toOrsRequest(List<ClientDTO> clients, Double startLat, Double startLon) {
        List<OrsJobDTO> jobs = mapClientsToJobs(clients);
        OrsVehicleDTO vehicle = createVehicle(startLat, startLon);
        return new OrsOptimizationRequestDTO(jobs, Collections.singletonList(vehicle));
    }

    List<OrsJobDTO> mapClientsToJobs(List<ClientDTO> clients);

    @Mapping(target = "id", source = "instalacao")
    @Mapping(target = "service", constant = "300")
    @Mapping(target = "location", source = "client", qualifiedByName = "mapLocation")
    OrsJobDTO toOrsJob(ClientDTO client);

    @Named("mapLocation")
    default List<Double> mapLocation(ClientDTO client) {
        return List.of(client.longitude(), client.latitude());
    }

    default OrsVehicleDTO createVehicle(Double startLat, Double startLon) {
        List<Double> startLocation = List.of(startLon, startLat);
        return new OrsVehicleDTO(
                1L,
                "driving-car",
                startLocation,
                startLocation
        );
    }
}
