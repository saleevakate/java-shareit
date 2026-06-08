package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestCreateDto requestDto, Integer userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = ItemRequestMapper.toItemRequest(requestDto, requestor);
        request = requestRepository.save(request);
        return ItemRequestMapper.toItemRequestDto(request, List.of());
    }

    @Override
    public List<ItemRequestDto> getByUser(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<ItemRequest> requests = requestRepository.findByRequestorId(
                userId, Sort.by(Sort.Direction.DESC, "created"));

        return fillRequestsWithItems(requests);
    }

    @Override
    public List<ItemRequestDto> getAllOther(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<ItemRequest> requests = requestRepository.findByRequestorIdNot(
                userId, Sort.by(Sort.Direction.DESC, "created"));

        return fillRequestsWithItems(requests);
    }

    @Override
    public ItemRequestDto getById(Integer requestId, Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        List<ru.practicum.shareit.item.model.Item> items = itemRepository.findByRequestId(requestId);
        List<ItemResponseDto> itemDtos = items.stream()
                .map(ItemRequestMapper::toItemResponseDto)
                .collect(Collectors.toList());

        return ItemRequestMapper.toItemRequestDto(request, itemDtos);
    }

    private List<ItemRequestDto> fillRequestsWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }

        List<Integer> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<ru.practicum.shareit.item.model.Item> allItems = itemRepository.findByRequestIdIn(requestIds);
        Map<Integer, List<ru.practicum.shareit.item.model.Item>> itemsByRequestId = allItems.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> {
                    List<ru.practicum.shareit.item.model.Item> items = itemsByRequestId.getOrDefault(request.getId(), List.of());
                    List<ItemResponseDto> itemDtos = items.stream()
                            .map(ItemRequestMapper::toItemResponseDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toItemRequestDto(request, itemDtos);
                })
                .collect(Collectors.toList());
    }
}
