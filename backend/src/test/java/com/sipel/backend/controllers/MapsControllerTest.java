package com.sipel.backend.controllers;

import com.sipel.backend.services.MapsService;
import com.sipel.backend.services.TokenService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MapsController.class)
@AutoConfigureMockMvc(addFilters = false)
class MapsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MapsService mapsService;

    @MockBean
    private MeterRegistry meterRegistry;

    @MockBean
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        Counter mockCounter = mock(Counter.class);
        when(meterRegistry.counter(anyString())).thenReturn(mockCounter);
    }

    @Test
    @DisplayName("Should redirect to Google Maps URL")
    void shouldRedirect() throws Exception {
        Double lat = -23.5505;
        Double lon = -46.6333;
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("https://www.google.com/maps?q=-23.5505,-46.6333"));

        when(mapsService.createMapsUrl(lat, lon)).thenReturn(headers);

        mockMvc.perform(get("/api/v1/maps/redirect")
                        .param("latitude", lat.toString())
                        .param("longitude", lon.toString()))
                .andExpect(status().isFound()) // 302
                .andExpect(header().string("Location", "https://www.google.com/maps?q=-23.5505,-46.6333"));
    }
}
