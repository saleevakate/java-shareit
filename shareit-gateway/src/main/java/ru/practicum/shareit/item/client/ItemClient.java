package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .rootUri(serverUrl + API_PREFIX)
                .build());
    }

    public ResponseEntity<Object> create(ItemDto itemDto, int userId) {
        return post("", (long) userId, itemDto);
    }

    public ResponseEntity<Object> update(int itemId, ItemDto itemDto, int userId) {
        return patch("/" + itemId, (long) userId, itemDto);
    }

    public ResponseEntity<Object> getById(int itemId, int userId) {
        return get("/" + itemId, (long) userId);
    }

    public ResponseEntity<Object> getByOwner(int userId) {
        return get("", (long) userId);
    }

    public ResponseEntity<Object> search(String text, int userId) {
        Map<String, Object> params = Map.of("text", text);
        return get("/search?text={text}", (long) userId, params);
    }

    public ResponseEntity<Object> addComment(int itemId, CommentCreateDto commentDto, int userId) {
        return post("/" + itemId + "/comment", (long) userId, commentDto);
    }
}