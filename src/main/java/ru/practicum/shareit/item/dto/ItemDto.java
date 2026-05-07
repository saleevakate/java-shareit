package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor  // нужен для создания объекта с полями
public class ItemDto {
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;  // только ID запроса, не весь объект
}