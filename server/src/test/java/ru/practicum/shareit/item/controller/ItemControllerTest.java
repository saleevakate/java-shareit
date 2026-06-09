package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);

        commentDto = new CommentDto();
        commentDto.setId(1);
        commentDto.setText("Great item!");
        commentDto.setAuthorName("User");
        commentDto.setCreated(LocalDateTime.now());
    }

    @Test
    void create_shouldReturnItem() throws Exception {
        when(itemService.create(any(ItemDto.class), eq(1))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void getById_shouldReturnItem() throws Exception {
        ItemWithBookingsDto responseDto = new ItemWithBookingsDto(itemDto);
        responseDto.setComments(List.of());

        when(itemService.getById(1, 1)).thenReturn(responseDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getByOwner_shouldReturnItems() throws Exception {
        ItemWithBookingsDto itemWithBookingsDto = new ItemWithBookingsDto(itemDto);
        when(itemService.getByOwner(1)).thenReturn(List.of(itemWithBookingsDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void search_shouldReturnItems() throws Exception {
        when(itemService.search("drill", 1)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_shouldReturnComment() throws Exception {
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setText("Great item!");

        when(itemService.addComment(eq(1), any(CommentCreateDto.class), eq(2))).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great item!"));
    }

    @Test
    void update_shouldReturnUpdatedItem() throws Exception {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Drill");

        when(itemService.update(eq(1), any(ItemDto.class), eq(1))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_shouldReturnNotFound_whenItemDoesNotExist() throws Exception {
        when(itemService.update(eq(999), any(ItemDto.class), eq(1)))
                .thenThrow(new NotFoundException("Вещь не найдена"));

        mockMvc.perform(patch("/items/999")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_shouldReturnNotFound_whenItemDoesNotExist() throws Exception {
        when(itemService.getById(999, 1)).thenThrow(new NotFoundException("Вещь не найдена"));

        mockMvc.perform(get("/items/999")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByOwner_shouldReturnEmptyList_whenUserHasNoItems() throws Exception {
        when(itemService.getByOwner(1)).thenReturn(List.of());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void search_shouldReturnEmptyList_whenTextIsBlank() throws Exception {
        when(itemService.search("", 1)).thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", "")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void addComment_shouldReturnNotFound_whenItemDoesNotExist() throws Exception {
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setText("Great item!");

        when(itemService.addComment(eq(999), any(CommentCreateDto.class), eq(2)))
                .thenThrow(new NotFoundException("Вещь не найдена"));

        mockMvc.perform(post("/items/999/comment")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound());
    }
}
