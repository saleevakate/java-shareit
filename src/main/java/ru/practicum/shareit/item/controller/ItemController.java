package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("POST /items - создание вещи: {}, userId={}", itemDto, userId);
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @PathVariable int itemId,
            @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("PATCH /items/{} - обновление вещи: {}, userId={}", itemId, itemDto, userId);
        return itemService.update(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getById(@PathVariable int itemId,
                                       @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /items/{} - просмотр вещи, userId={}", itemId, userId);
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getByOwner(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /items - список вещей владельца, userId={}", userId);
        return itemService.getByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam String text,
            @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /items/search - поиск вещей, text={}, userId={}", text, userId);
        return itemService.search(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @PathVariable int itemId,
            @Valid @RequestBody CommentCreateDto commentDto,
            @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("POST /items/{}/comment - добавление комментария: {}, userId={}",
                itemId, commentDto.getText(), userId);
        return itemService.addComment(itemId, commentDto, userId);
    }
}