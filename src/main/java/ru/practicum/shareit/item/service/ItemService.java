package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, int userId);

    ItemDto update(int itemId, ItemDto itemDto, int userId);

    ItemDto getById(int itemId, int userId);

    List<ItemDto> getByOwner(int userId);

    List<ItemDto> search(String text, int userId);
}