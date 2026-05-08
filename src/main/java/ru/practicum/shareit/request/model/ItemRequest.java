package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {

    private int id;
    private String descriptions;
    private User requestor;
    private LocalDateTime created;
}
