package org.codevoke.probnb.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codevoke.probnb.dto.ReservationDTO;
import org.codevoke.probnb.service.ReservationService;
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
class ReservationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReservationService reservationService;

    private ReservationController reservationController;

    private ObjectMapper objectMapper;

    private ReservationDTO testReservationDTO;
    private List<ReservationDTO> testReservationList;

    @BeforeEach
    void setUp() {
        reservationController = new ReservationController(reservationService);
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
        objectMapper = new ObjectMapper();
        
        testReservationDTO = new ReservationDTO();
        testReservationDTO.setId(1L);
        testReservationDTO.setRoomId(1L);
        testReservationDTO.setUserId(1L);
        testReservationDTO.setHostId(2L);
        testReservationDTO.setDateStart(ZonedDateTime.now().plusHours(1));
        testReservationDTO.setDateEnd(ZonedDateTime.now().plusHours(2));

        testReservationList = Arrays.asList(testReservationDTO);
    }

    @Test
    void getReservationById_WhenReservationExists_ShouldReturnReservation() throws Exception {
        // given
        when(reservationService.findReservationById(1L)).thenReturn(testReservationDTO);

        // when & then
        mockMvc.perform(get("/api/users/1/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.room_id").value(1))
                .andExpect(jsonPath("$.user_id").value(1))
                .andExpect(jsonPath("$.host_id").value(2));
    }

    @Test
    void getReservationsByUserId_WhenReservationsExist_ShouldReturnReservationList() throws Exception {
        // given
        when(reservationService.findReservationsByUserId(1L)).thenReturn(testReservationList);

        // when & then
        mockMvc.perform(get("/api/users/1/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].room_id").value(1))
                .andExpect(jsonPath("$[0].user_id").value(1))
                .andExpect(jsonPath("$[0].host_id").value(2));
    }

    @Test
    void createReservation_WhenValidData_ShouldReturnCreatedReservation() throws Exception {
        // given
        ReservationDTO createDTO = new ReservationDTO();
        createDTO.setRoomId(1L);
        createDTO.setUserId(1L);
        createDTO.setHostId(2L);
        createDTO.setDateStart(ZonedDateTime.now().plusHours(1));
        createDTO.setDateEnd(ZonedDateTime.now().plusHours(2));

        when(reservationService.createReservation(any(ReservationDTO.class), eq(1L))).thenReturn(testReservationDTO);

        // when & then
        mockMvc.perform(post("/api/users/1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.room_id").value(1));
    }

    @Test
    void createReservation_WhenInvalidData_ShouldReturnBadRequest() throws Exception {
        // given
        ReservationDTO invalidDTO = new ReservationDTO();
        // Missing required fields

        // when & then
        mockMvc.perform(post("/api/users/1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReservation_WhenPastDate_ShouldReturnBadRequest() throws Exception {
        // given
        ReservationDTO pastDateDTO = new ReservationDTO();
        pastDateDTO.setRoomId(1L);
        pastDateDTO.setUserId(1L);
        pastDateDTO.setHostId(2L);
        pastDateDTO.setDateStart(ZonedDateTime.now().minusHours(1)); // Past date
        pastDateDTO.setDateEnd(ZonedDateTime.now().plusHours(1));

        // when & then
        mockMvc.perform(post("/api/users/1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pastDateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateReservation_WhenValidData_ShouldReturnUpdatedReservation() throws Exception {
        // given
        ReservationDTO updateDTO = new ReservationDTO();
        updateDTO.setDateStart(ZonedDateTime.now().plusHours(3));
        updateDTO.setDateEnd(ZonedDateTime.now().plusHours(4));

        when(reservationService.updateReservation(eq(1L), any(ReservationDTO.class))).thenReturn(testReservationDTO);

        // when & then
        mockMvc.perform(patch("/api/users/1/reservations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteReservation_WhenReservationExists_ShouldReturnDeletedReservation() throws Exception {
        // given
        when(reservationService.deleteReservation(1L)).thenReturn(testReservationDTO);

        // when & then
        mockMvc.perform(delete("/api/users/1/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createReservation_WhenEndDateBeforeStartDate_ShouldReturnBadRequest() throws Exception {
        // given
        ReservationDTO invalidDateDTO = new ReservationDTO();
        invalidDateDTO.setRoomId(1L);
        invalidDateDTO.setUserId(1L);
        invalidDateDTO.setHostId(2L);
        invalidDateDTO.setDateStart(ZonedDateTime.now().plusHours(2));
        invalidDateDTO.setDateEnd(ZonedDateTime.now().plusHours(1)); // End before start

        // when & then
        mockMvc.perform(post("/api/users/1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReservation_WhenSameStartAndEndDate_ShouldReturnBadRequest() throws Exception {
        // given
        ZonedDateTime sameTime = ZonedDateTime.now().plusHours(1);
        ReservationDTO sameDateDTO = new ReservationDTO();
        sameDateDTO.setRoomId(1L);
        sameDateDTO.setUserId(1L);
        sameDateDTO.setHostId(2L);
        sameDateDTO.setDateStart(sameTime);
        sameDateDTO.setDateEnd(sameTime); // Same time

        // when & then
        mockMvc.perform(post("/api/users/1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sameDateDTO)))
                .andExpect(status().isBadRequest());
    }
} 