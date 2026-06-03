package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.StatusBooking;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDto {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer itemId;
    private String itemName;
    private Integer bookerId;
    private String bookerName;
    private StatusBooking status;
}