package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestCreateDto requestDto,
                                         @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("POST /requests - создание запроса: {}, userId={}", requestDto, userId);
        return itemRequestClient.create(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getByUser(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /requests - список своих запросов, userId={}", userId);
        return itemRequestClient.getByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOther(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /requests/all - список чужих запросов, userId={}", userId);
        return itemRequestClient.getAllOther(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable int requestId,
                                          @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /requests/{} - просмотр запроса, userId={}", requestId, userId);
        return itemRequestClient.getById(requestId, userId);
    }
}
