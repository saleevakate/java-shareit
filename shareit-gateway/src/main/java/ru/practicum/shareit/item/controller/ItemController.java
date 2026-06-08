package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemDto;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                         @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("POST /items - создание вещи: {}, userId={}", itemDto, userId);
        return itemClient.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable int itemId,
                                         @RequestBody ItemDto itemDto,
                                         @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("PATCH /items/{} - обновление вещи: {}, userId={}", itemId, itemDto, userId);
        return itemClient.update(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable int itemId,
                                          @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /items/{} - просмотр вещи, userId={}", itemId, userId);
        return itemClient.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getByOwner(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /items - список вещей владельца, userId={}", userId);
        return itemClient.getByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /items/search - поиск вещей, text={}, userId={}", text, userId);
        return itemClient.search(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable int itemId,
                                             @Valid @RequestBody CommentCreateDto commentDto,
                                             @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("POST /items/{}/comment - добавление комментария: {}, userId={}",
                itemId, commentDto.getText(), userId);
        return itemClient.addComment(itemId, commentDto, userId);
    }
}