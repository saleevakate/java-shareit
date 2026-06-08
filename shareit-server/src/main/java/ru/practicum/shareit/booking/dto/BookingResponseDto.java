package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.StatusBooking;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDto {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemInfo item;
    private BookerInfo booker;
    private StatusBooking status;

    @Data
    @AllArgsConstructor
    public static class ItemInfo {
        private Integer id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class BookerInfo {
        private Integer id;
        private String name;
    }
}