package com.sipel.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipel.backend.domain.Equipes;
import com.sipel.backend.domain.ExecucoesRota;
import com.sipel.backend.dtos.RouteRequestDTO;
import com.sipel.backend.dtos.ors.OrsOptimizationRequestDTO;
import com.sipel.backend.dtos.ors.OrsOptimizationResponseDTO;
import com.sipel.backend.exceptions.RouteProcessingException;
import com.sipel.backend.mappers.RouteHistoryMapper;
import com.sipel.backend.mappers.RouteMapper;
import com.sipel.backend.repositories.EquipesRepository;
import com.sipel.backend.repositories.ExecucoesRotaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import jakarta.validation.Valid;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class RouteService {

    private final EquipesRepository equipesRepository;
    private final ExecucoesRotaRepository execucoesRotaRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final WebClient.Builder webClientBuilder;
    private final RouteMapper routeMapper;
    private final RouteHistoryMapper routeHistoryMapper;

    @Value("${app.openrouteservice.url}")
    private String orsUrl;

    @Value("${app.openrouteservice.api-key}")
    private String orsApiKey;

    @CacheEvict(value = "historico_rotas", key = "#request.teamId()")
    public OrsOptimizationResponseDTO calculateRoute(@Valid RouteRequestDTO request) throws JsonProcessingException {
        Double startLat = request.currentLat();
        Double startLon = request.currentLon();

        if (startLat == null || startLon == null) {
            Equipes equipe = equipesRepository.findById(request.teamId())
                    .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada com ID: " + request.teamId()));
            startLat = equipe.getLatitudeBase();
            startLon = equipe.getLongitudeBase();
        }

        LocalDate today = LocalDate.now();
        String clientsHash = String.valueOf(request.clients().hashCode());
        // Incluir coordenadas de inicio na chave de cache para diferenciar rotas saindo da base vs local atual
        String startPointKey = String.format("%.4f,%.4f", startLat, startLon);
        String cacheKey = String.format("rota:equipe:%d:data:%s:start:%s:clients:%s", request.teamId(), today, startPointKey, clientsHash);

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

    @Transactional(readOnly = true)
    @Cacheable(value = "historico_rotas", key = "#teamId")
    public java.util.List<com.sipel.backend.dtos.RouteHistoryDTO> getRouteHistory(Long teamId) {
        if (!equipesRepository.existsById(teamId)) {
            throw new EntityNotFoundException("Equipe não encontrada com ID: " + teamId);
        }
        return routeHistoryMapper.toDTOList(execucoesRotaRepository.findAllByEquipeIdOrderByDataDesc(teamId));
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // Toda meia-noite
    @CacheEvict(value = "historico_rotas", allEntries = true)
    public void cleanupOldRoutes() {
        LocalDate cutoffDate = LocalDate.now().minusDays(2);
        execucoesRotaRepository.deleteOlderThan(cutoffDate);
    }

    @Transactional(readOnly = true)
    public OrsOptimizationResponseDTO getRouteDetails(String id) throws JsonProcessingException {
        ExecucoesRota execution = execucoesRotaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rota não encontrada com ID: " + id));
        
        return objectMapper.readValue(execution.getRotaJson(), OrsOptimizationResponseDTO.class);
    }
}
