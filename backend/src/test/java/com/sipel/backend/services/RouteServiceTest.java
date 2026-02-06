package com.sipel.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipel.backend.domain.Equipes;
import com.sipel.backend.domain.ExecucoesRota;
import com.sipel.backend.dtos.ClientDTO;
import com.sipel.backend.dtos.RouteRequestDTO;
import com.sipel.backend.dtos.ors.OrsOptimizationRequestDTO;
import com.sipel.backend.dtos.ors.OrsOptimizationResponseDTO;
import com.sipel.backend.dtos.ors.OrsSummaryDTO;
import com.sipel.backend.repositories.EquipesRepository;
import com.sipel.backend.repositories.ExecucoesRotaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.sipel.backend.mappers.RouteMapper;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    private EquipesRepository equipesRepository;

    @Mock
    private ExecucoesRotaRepository execucoesRotaRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private RouteMapper routeMapper;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private RouteService routeService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(routeService, "orsUrl", "http://mock-ors-url");
        ReflectionTestUtils.setField(routeService, "orsApiKey", "mock-api-key");
    }

    @Test
    @DisplayName("Should return cached route if available")
    void shouldReturnCachedRoute() throws Exception {
        // Arrange
        RouteRequestDTO request = new RouteRequestDTO(1L, Collections.emptyList(), -23.0, -46.0);
        String clientsHash = String.valueOf(request.clients().hashCode());
        String cacheKey = String.format("rota:equipe:%d:data:%s:clients:%s", request.teamId(), LocalDate.now(), clientsHash);
        String cachedJson = "{\"summary\":{\"cost\":100.0}}";
        OrsOptimizationResponseDTO expectedResponse = new OrsOptimizationResponseDTO(null, new OrsSummaryDTO(100.0, 0.0, 0.0));

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(cacheKey)).thenReturn(cachedJson);
        when(objectMapper.readValue(cachedJson, OrsOptimizationResponseDTO.class)).thenReturn(expectedResponse);

        // Act
        OrsOptimizationResponseDTO result = routeService.calculateRoute(request);

        // Assert
        assertNotNull(result);
        assertEquals(100.0, result.summary().cost());
        verify(webClientBuilder, never()).build();
    }

    @Test
    @DisplayName("Should fetch from API if cache miss, and save to DB/Redis")
    void shouldFetchFromApiAndSave() throws Exception {
        // Arrange
        RouteRequestDTO request = new RouteRequestDTO(1L, List.of(new ClientDTO(1L, -23.0, -46.0)), -23.5, -46.5);
        String clientsHash = String.valueOf(request.clients().hashCode());
        String cacheKey = String.format("rota:equipe:%d:data:%s:clients:%s", request.teamId(), LocalDate.now(), clientsHash);
        OrsOptimizationResponseDTO apiResponse = new OrsOptimizationResponseDTO(Collections.emptyList(), new OrsSummaryDTO(200.0, 100.0, 100.0));
        String jsonResponse = "{\"summary\":{\"cost\":200.0}}";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(cacheKey)).thenReturn(null);
        
        // WebClient mocking
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(OrsOptimizationRequestDTO.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OrsOptimizationResponseDTO.class)).thenReturn(Mono.just(apiResponse));

        when(objectMapper.writeValueAsString(apiResponse)).thenReturn(jsonResponse);
        when(routeMapper.toOrsRequest(anyList(), anyDouble(), anyDouble())).thenReturn(new OrsOptimizationRequestDTO(Collections.emptyList(), Collections.emptyList()));

        // Act
        OrsOptimizationResponseDTO result = routeService.calculateRoute(request);

        // Assert
        assertNotNull(result);
        assertEquals(200.0, result.summary().cost());
        
        verify(redisTemplate.opsForValue()).set(eq(cacheKey), eq(jsonResponse), any(Duration.class));
        verify(execucoesRotaRepository).save(any(ExecucoesRota.class));
    }

    @Test
    @DisplayName("Should use team base location if current location is null")
    void shouldUseTeamBaseLocation() throws Exception {
        // Arrange
        RouteRequestDTO request = new RouteRequestDTO(1L, List.of(new ClientDTO(1L, -23.0, -46.0)), null, null);
        Equipes equipe = new Equipes(1L, "Team A", -10.0, -20.0, com.sipel.backend.domain.enums.SetorEnum.LEITURA);
        String clientsHash = String.valueOf(request.clients().hashCode());
        String cacheKey = String.format("rota:equipe:%d:data:%s:clients:%s", request.teamId(), LocalDate.now(), clientsHash);
        OrsOptimizationResponseDTO apiResponse = new OrsOptimizationResponseDTO(Collections.emptyList(), new OrsSummaryDTO(300.0, 0.0, 0.0));

        when(equipesRepository.findById(1L)).thenReturn(Optional.of(equipe));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(cacheKey)).thenReturn(null);

        // WebClient mocking
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(OrsOptimizationRequestDTO.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OrsOptimizationResponseDTO.class)).thenReturn(Mono.just(apiResponse));
        
        when(objectMapper.writeValueAsString(apiResponse)).thenReturn("{}");
        when(routeMapper.toOrsRequest(anyList(), anyDouble(), anyDouble())).thenReturn(new OrsOptimizationRequestDTO(Collections.emptyList(), Collections.emptyList()));

        // Act
        routeService.calculateRoute(request);

        // Assert
        verify(equipesRepository).findById(1L);
        // Verify that the mapper was called with the team's base location
        verify(routeMapper).toOrsRequest(anyList(), eq(-10.0), eq(-20.0));
    }
    
    @Test
    @DisplayName("Should throw EntityNotFoundException if team not found and no current location")
    void shouldThrowExceptionWhenTeamNotFound() {
        RouteRequestDTO request = new RouteRequestDTO(99L, Collections.emptyList(), null, null);
        
        when(equipesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> routeService.calculateRoute(request));
    }
}
