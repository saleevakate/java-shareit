package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestCreateDto requestDto,
                                 @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("POST /requests - создание запроса: {}, userId={}", requestDto, userId);
        return requestService.create(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getByUser(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /requests - список своих запросов, userId={}", userId);
        return requestService.getByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllOther(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /requests/all - список чужих запросов, userId={}", userId);
        return requestService.getAllOther(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable int requestId,
                                  @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /requests/{} - просмотр запроса, userId={}", requestId, userId);
        return requestService.getById(requestId, userId);
    }
}