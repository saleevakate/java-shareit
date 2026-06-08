package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentRepository;
import ru.practicum.shareit.exception.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, int userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        item.setAvailable(itemDto.getAvailable());

        if (itemDto.getRequestId() != null) {
            ItemRequest request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));
            item.setRequest(request);
        }

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
    public ItemWithBookingsDto getById(int itemId, int userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        List<CommentDto> comments = getCommentsForItem(itemId);
        ItemWithBookingsDto dto = new ItemWithBookingsDto(ItemMapper.toItemDto(item));
        dto.setComments(comments);
        if (item.getOwner().getId() == userId) {
            LocalDateTime now = LocalDateTime.now();

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
        }
        return dto;
    }

    @Override
    public List<ItemWithBookingsDto> getByOwner(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Item> items = itemRepository.findByOwnerId(userId);
        List<Integer> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        Map<Integer, List<CommentDto>> commentsMap = getCommentsForItems(itemIds);

        return items.stream()
                .map(item -> {
                    List<CommentDto> comments = commentsMap.getOrDefault(item.getId(), List.of());
                    return toItemWithBookingsDto(item, comments);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text, int userId) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.searchAvailable(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(int itemId, CommentCreateDto commentDto, int userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        boolean hasCompletedBooking = bookingRepository
                .existsByItemIdAndBookerIdAndStatusAndEndBefore(
                        itemId, userId, StatusBooking.APPROVED, LocalDateTime.now());

        if (!hasCompletedBooking) {
            throw new MethodArgumentNotValidException("Пользователь не брал эту вещь в аренду или аренда ещё не завершена");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, author);
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    private List<CommentDto> getCommentsForItem(Integer itemId) {
        return commentRepository.findByItemIdOrderByCreatedDesc(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private Map<Integer, List<CommentDto>> getCommentsForItems(List<Integer> itemIds) {
        List<Comment> comments = commentRepository.findByItemIdIn(itemIds);
        return comments.stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::toCommentDto, Collectors.toList())
                ));
    }

    private ItemWithBookingsDto toItemWithBookingsDto(Item item, List<CommentDto> comments) {
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

        dto.setComments(comments);
        return dto;
    }
}