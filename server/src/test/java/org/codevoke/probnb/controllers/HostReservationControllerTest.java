package org.codevoke.probnb.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codevoke.probnb.dto.HostReservationDTO;
import org.codevoke.probnb.service.HostReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HostReservationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HostReservationService hostReservationService;

    private HostReservationController hostReservationController;

    private ObjectMapper objectMapper;

    private HostReservationDTO testHostReservationDTO;
    private List<HostReservationDTO> testHostReservationList;

    @BeforeEach
    void setUp() {
        hostReservationController = new HostReservationController(hostReservationService);
        mockMvc = MockMvcBuilders.standaloneSetup(hostReservationController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Поддержка ZonedDateTime
        
        testHostReservationDTO = new HostReservationDTO();
        testHostReservationDTO.setId(1L);
        testHostReservationDTO.setRoomId(1L);
        testHostReservationDTO.setHostId(2L);
        testHostReservationDTO.setDateStart(ZonedDateTime.now().plusHours(1));
        testHostReservationDTO.setDateEnd(ZonedDateTime.now().plusHours(2));

        testHostReservationList = Arrays.asList(testHostReservationDTO);
    }

    @Test
    void getHostReservationById_WhenReservationExists_ShouldReturnReservation() throws Exception {
        // given
        when(hostReservationService.findHostReservationById(1L)).thenReturn(testHostReservationDTO);

        // when & then
        mockMvc.perform(get("/users/2/host-reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.room_id").value(1))
                .andExpect(jsonPath("$.host_id").value(2));
    }

    @Test
    void getHostReservationsByHostId_WhenReservationsExist_ShouldReturnReservationList() throws Exception {
        // given
        when(hostReservationService.findHostReservationsByHostId(2L)).thenReturn(testHostReservationList);

        // when & then
        mockMvc.perform(get("/users/2/host-reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].room_id").value(1))
                .andExpect(jsonPath("$[0].host_id").value(2));
    }

    @Test
    void createHostReservation_WhenValidData_ShouldReturnCreatedReservation() throws Exception {
        // given
        HostReservationDTO createDTO = new HostReservationDTO();
        createDTO.setRoomId(1L);
        createDTO.setHostId(2L);
        createDTO.setDateStart(ZonedDateTime.now().plusHours(1));
        createDTO.setDateEnd(ZonedDateTime.now().plusHours(2));

        when(hostReservationService.createHostReservation(any(HostReservationDTO.class), eq(2L))).thenReturn(testHostReservationDTO);

        // when & then
        mockMvc.perform(post("/users/2/host-reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.room_id").value(1));
    }

    @Test
    void createHostReservation_WhenInvalidData_ShouldReturnBadRequest() throws Exception {
        // given
        HostReservationDTO invalidDTO = new HostReservationDTO();
        // Missing required fields

        // when & then
        mockMvc.perform(post("/users/2/host-reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createHostReservation_WhenPastDate_ShouldReturnBadRequest() throws Exception {
        // given
        HostReservationDTO pastDateDTO = new HostReservationDTO();
        pastDateDTO.setRoomId(1L);
        pastDateDTO.setHostId(2L);
        pastDateDTO.setDateStart(ZonedDateTime.now().minusHours(1)); // Past date
        pastDateDTO.setDateEnd(ZonedDateTime.now().plusHours(1));

        // when & then
        mockMvc.perform(post("/users/2/host-reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pastDateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createHostReservation_WhenEndDateBeforeStartDate_ShouldReturnBadRequest() throws Exception {
        // given
        HostReservationDTO invalidDateDTO = new HostReservationDTO();
        invalidDateDTO.setRoomId(1L);
        invalidDateDTO.setHostId(2L);
        invalidDateDTO.setDateStart(ZonedDateTime.now().plusHours(2));
        invalidDateDTO.setDateEnd(ZonedDateTime.now().plusHours(1)); // End before start

        // when & then
        mockMvc.perform(post("/users/2/host-reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createHostReservation_WhenSameStartAndEndDate_ShouldReturnBadRequest() throws Exception {
        // given
        ZonedDateTime sameTime = ZonedDateTime.now().plusHours(1);
        HostReservationDTO sameDateDTO = new HostReservationDTO();
        sameDateDTO.setRoomId(1L);
        sameDateDTO.setHostId(2L);
        sameDateDTO.setDateStart(sameTime);
        sameDateDTO.setDateEnd(sameTime); // Same time

        // when & then
        mockMvc.perform(post("/users/2/host-reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sameDateDTO)))
                .andExpect(status().isBadRequest());
    }
} 