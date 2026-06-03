package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, int userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);

        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(int itemId, ItemDto itemDto, int userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("Редактировать вещь может только владелец");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(int itemId, int userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (item.getOwner().getId() == userId) {
            return toItemWithBookingsDto(item);
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getByOwner(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Item> items = itemRepository.findByOwnerId(userId);
        LocalDateTime now = LocalDateTime.now();

        return items.stream()
                .map(item -> {
                    if (item.getOwner().getId() == userId) {
                        return toItemWithBookingsDto(item);
                    }
                    return ItemMapper.toItemDto(item);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text, int userId) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String lowerText = text.toLowerCase();
        return itemRepository.findAll().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lowerText) ||
                        item.getDescription().toLowerCase().contains(lowerText))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private ItemWithBookingsDto toItemWithBookingsDto(Item item) {
        LocalDateTime now = LocalDateTime.now();
        ItemWithBookingsDto dto = new ItemWithBookingsDto(ItemMapper.toItemDto(item));
        List<Booking> lastBookings = bookingRepository.findLastBooking(item.getId(), now);
        if (!lastBookings.isEmpty()) {
            Booking lastBooking = lastBookings.get(0);
            dto.setLastBooking(new BookingShortDto(
                    lastBooking.getId(),
                    lastBooking.getBooker().getId(),
                    lastBooking.getStart(),
                    lastBooking.getEnd()
            ));
        }

        List<Booking> nextBookings = bookingRepository.findNextBooking(item.getId(), now);
        if (!nextBookings.isEmpty()) {
            Booking nextBooking = nextBookings.get(0);
            dto.setNextBooking(new BookingShortDto(
                    nextBooking.getId(),
                    nextBooking.getBooker().getId(),
                    nextBooking.getStart(),
                    nextBooking.getEnd()
            ));
        }

        // TODO: добавить комментарии позже
        dto.setComments(List.of());

        return dto;
    }
}