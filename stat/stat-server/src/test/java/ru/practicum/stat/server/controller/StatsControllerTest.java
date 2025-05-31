package ru.practicum.stat.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;
import ru.practicum.stat.server.model.EndpointHitEntity;
import ru.practicum.stat.server.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StatsControllerTest {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StatsService statsService;

    @Test
    public void givenValidParams_whenGetStats_shouldParseDatesCorrectly() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);

        ViewStats viewStats = new ViewStats();
        viewStats.setApp("testApp");
        viewStats.setUri("/test");
        viewStats.setHits(10L);

        when(statsService.getStats(any(), any(), any(), anyBoolean())).thenReturn(Collections.singletonList(viewStats));

        String startEncoded = start.format(DATE_TIME_FORMATTER);
        String endEncoded = end.format(DATE_TIME_FORMATTER);

        mockMvc.perform(get("/stats").param("start", startEncoded).param("end", endEncoded)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        ArgumentCaptor<LocalDateTime> startCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> endCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(statsService).getStats(startCaptor.capture(), endCaptor.capture(), any(), anyBoolean());

        assertEquals(start.truncatedTo(java.time.temporal.ChronoUnit.SECONDS), startCaptor.getValue().truncatedTo(java
                .time.temporal.ChronoUnit.SECONDS));
        assertEquals(end.truncatedTo(java.time.temporal.ChronoUnit.SECONDS), endCaptor.getValue().truncatedTo(java.time
                .temporal.ChronoUnit.SECONDS));
    }

    @Test
    public void givenInvalidParams_whenGetStats_shouldReturnBadRequest() throws Exception {
        LocalDateTime futureStart = LocalDateTime.now().plusDays(1);
        LocalDateTime now = LocalDateTime.now();

        mockMvc.perform(get("/stats").param("startTime", futureStart.format(DATE_TIME_FORMATTER))
                        .param("end", now.format(DATE_TIME_FORMATTER)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenValidHit_whenPostHit_shouldReturnCreated() throws Exception {
        EndpointHit validHit = new EndpointHit();
        validHit.setApp("testApp");
        validHit.setUri("/test");
        validHit.setIp("127.0.0.1");
        validHit.setTimestamp(LocalDateTime.now());

        when(statsService.saveHit(any(EndpointHit.class))).thenReturn(validHit);

        mockMvc.perform(post("/hit").contentType(MediaType.APPLICATION_JSON).content(objectMapper
                .writeValueAsString(validHit))).andExpect(status().isCreated());
    }


    @Test
    public void givenInvalidHit_whenPostHit_shouldReturnBadRequest() throws Exception {
        EndpointHitEntity invalidHit = new EndpointHitEntity();

        mockMvc.perform(post("/hit").contentType(MediaType.APPLICATION_JSON).content(objectMapper
                .writeValueAsString(invalidHit))).andExpect(status().isBadRequest());
    }
}
