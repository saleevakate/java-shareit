package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingRequestDto requestDto;
    private BookingResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new BookingRequestDto();
        requestDto.setItemId(1);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        responseDto = new BookingResponseDto();
        responseDto.setId(1);
        responseDto.setStart(LocalDateTime.now().plusDays(1));
        responseDto.setEnd(LocalDateTime.now().plusDays(2));
        responseDto.setItem(new BookingResponseDto.ItemInfo(1, "Drill"));
        responseDto.setBooker(new BookingResponseDto.BookerInfo(2, "Booker"));
        responseDto.setStatus(StatusBooking.WAITING);
    }

    @Test
    void create_shouldReturnBooking() throws Exception {
        when(bookingService.create(any(BookingRequestDto.class), eq(1))).thenReturn(responseDto);

        mockMvc.perform(post("/bookings").header("X-Sharer-User-Id", 1).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approve_shouldReturnApprovedBooking() throws Exception {
        responseDto.setStatus(StatusBooking.APPROVED);
        when(bookingService.approve(eq(1), eq(true), eq(1))).thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/1").param("approved", "true").header("X-Sharer-User-Id", 1)).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getById_shouldReturnBooking() throws Exception {
        when(bookingService.getById(1, 1)).thenReturn(responseDto);

        mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 1)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getByBooker_shouldReturnBookings() throws Exception {
        when(bookingService.getByBooker(1, "ALL")).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/bookings").param("state", "ALL").header("X-Sharer-User-Id", 1)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getByOwner_shouldReturnBookings() throws Exception {
        when(bookingService.getByOwner(1, "ALL")).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/bookings/owner").param("state", "ALL").header("X-Sharer-User-Id", 1)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void create_shouldReturnBadRequest_whenDatesInvalid() throws Exception {
        BookingRequestDto invalidDto = new BookingRequestDto();
        invalidDto.setItemId(1);
        invalidDto.setStart(LocalDateTime.now().minusDays(1));
        invalidDto.setEnd(LocalDateTime.now().plusDays(1));

        when(bookingService.create(any(BookingRequestDto.class), eq(1)))
                .thenThrow(new ru.practicum.shareit.exception.ValidationException("Дата начала не может быть в прошлом"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void approve_shouldReturnNotFound_whenBookingNotFound() throws Exception {
        when(bookingService.approve(eq(999), eq(true), eq(1)))
                .thenThrow(new NotFoundException("Бронирование не найдено"));

        mockMvc.perform(patch("/bookings/999")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_shouldReturnNotFound_whenBookingNotFound() throws Exception {
        when(bookingService.getById(999, 1))
                .thenThrow(new NotFoundException("Бронирование не найдено"));

        mockMvc.perform(get("/bookings/999")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByBooker_shouldReturnEmptyList_whenNoBookings() throws Exception {
        when(bookingService.getByBooker(1, "ALL")).thenReturn(List.of());

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getByBooker_shouldHandleAllStates() throws Exception {
        String[] states = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"};

        for (String state : states) {
            when(bookingService.getByBooker(1, state)).thenReturn(List.of(responseDto));
            mockMvc.perform(get("/bookings")
                            .param("state", state)
                            .header("X-Sharer-User-Id", 1))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void getByOwner_shouldReturnEmptyList_whenNoBookings() throws Exception {
        when(bookingService.getByOwner(1, "ALL")).thenReturn(List.of());

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getByOwner_shouldHandleAllStates() throws Exception {
        String[] states = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"};

        for (String state : states) {
            when(bookingService.getByOwner(1, state)).thenReturn(List.of(responseDto));
            mockMvc.perform(get("/bookings/owner")
                            .param("state", state)
                            .header("X-Sharer-User-Id", 1))
                    .andExpect(status().isOk());
        }
    }
}
