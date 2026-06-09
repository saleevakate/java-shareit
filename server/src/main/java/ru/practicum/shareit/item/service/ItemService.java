package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, int userId);

    ItemDto update(int itemId, ItemDto itemDto, int userId);

    ItemWithBookingsDto getById(int itemId, int userId);

    List<ItemWithBookingsDto> getByOwner(int userId);

    List<ItemDto> search(String text, int userId);

    CommentDto addComment(int itemId, CommentCreateDto commentDto, int userId);
}
