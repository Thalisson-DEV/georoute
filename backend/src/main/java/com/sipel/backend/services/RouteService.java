package com.sipel.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipel.backend.domain.Equipes;
import com.sipel.backend.domain.ExecucoesRota;
import com.sipel.backend.dtos.RouteRequestDTO;
import com.sipel.backend.dtos.ors.OrsOptimizationRequestDTO;
import com.sipel.backend.dtos.ors.OrsOptimizationResponseDTO;
import com.sipel.backend.exceptions.RouteProcessingException;
import com.sipel.backend.repositories.EquipesRepository;
import com.sipel.backend.repositories.ExecucoesRotaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

@Service
@Validated
public class RouteService {

    private final EquipesRepository equipesRepository;
    private final ExecucoesRotaRepository execucoesRotaRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final WebClient.Builder webClientBuilder;
    private final com.sipel.backend.mappers.RouteMapper routeMapper;

    @Value("${app.openrouteservice.url}")
    private String orsUrl;

    @Value("${app.openrouteservice.api-key}")
    private String orsApiKey;

    public RouteService(EquipesRepository equipesRepository, ExecucoesRotaRepository execucoesRotaRepository, StringRedisTemplate redisTemplate, ObjectMapper objectMapper, WebClient.Builder webClientBuilder, com.sipel.backend.mappers.RouteMapper routeMapper) {
        this.equipesRepository = equipesRepository;
        this.execucoesRotaRepository = execucoesRotaRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.webClientBuilder = webClientBuilder;
        this.routeMapper = routeMapper;
    }

    public OrsOptimizationResponseDTO calculateRoute(@Valid RouteRequestDTO request) throws IOException {
        Double startLat = request.currentLat();
        Double startLon = request.currentLon();

        if (startLat == null || startLon == null) {
            Equipes equipe = equipesRepository.findById(request.teamId())
                    .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada com ID: " + request.teamId()));
            startLat = equipe.getLatitudeBase();
            startLon = equipe.getLongitudeBase();
        }

        LocalDate today = LocalDate.now();
        String cacheKey = String.format("rota:equipe:%d:data:%s", request.teamId(), today);

        String cachedRoute = redisTemplate.opsForValue().get(cacheKey);
        if (cachedRoute != null) {
            String trimmedRoute = cachedRoute.trim();
            if (!trimmedRoute.startsWith("{") && !trimmedRoute.startsWith("[")) {
                 throw new RouteProcessingException("Cache inválido: Conteúdo não é um JSON válido.");
            }
            return objectMapper.readValue(trimmedRoute, OrsOptimizationResponseDTO.class);
        }

        OrsOptimizationRequestDTO orsRequest = routeMapper.toOrsRequest(request.clients(), startLat, startLon);

        OrsOptimizationResponseDTO response = webClientBuilder.build()
                .post()
                .uri(orsUrl)
                .header("Authorization", orsApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(orsRequest)
                .retrieve()
                .bodyToMono(OrsOptimizationResponseDTO.class)
                .block();

        if (response != null) {
            String jsonResponse = objectMapper.writeValueAsString(response);

            redisTemplate.opsForValue().set(cacheKey, jsonResponse, Duration.ofHours(24));

            ExecucoesRota execution = new ExecucoesRota();
            execution.setEquipeId(request.teamId());
            execution.setData(today);
            execution.setRotaJson(jsonResponse);

            Optional<ExecucoesRota> existing = execucoesRotaRepository.findByEquipeIdAndData(request.teamId(), today);
            existing.ifPresent(execucoesRota -> execution.setId(execucoesRota.getId()));

            execucoesRotaRepository.save(execution);
        }

        return response;
    }
}