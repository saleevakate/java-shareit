package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestCreateDto requestDto, Integer userId);

    List<ItemRequestDto> getByUser(Integer userId);

    List<ItemRequestDto> getAllOther(Integer userId);

    ItemRequestDto getById(Integer requestId, Integer userId);
}
