package ru.practicum.shareit.unit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentRepository;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private User booker;
    private Item item;
    private ItemDto itemDto;
    private ItemRequest request;
    private Comment comment;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        booker = new User();
        booker.setId(2);
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

        item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(null);  // по умолчанию без запроса

        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
        itemDto.setRequestId(null);

        request = new ItemRequest();
        request.setId(1);
        request.setDescription("Need a drill");
        request.setRequestor(booker);
        request.setCreated(LocalDateTime.now());

        comment = new Comment();
        comment.setId(1);
        comment.setText("Great item!");
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setCreated(LocalDateTime.now());

        booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(StatusBooking.APPROVED);
    }

    // ========== CREATE TESTS ==========

    @Test
    void create_shouldSaveItem_whenUserExists() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.create(itemDto, 1);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Drill");
    }

    @Test
    void create_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.create(itemDto, 99))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void create_shouldSaveItemWithRequest_whenRequestIdProvided() {
        // ✅ ИСПРАВЛЕНО: создаём отдельный item с request
        Item itemWithRequest = new Item();
        itemWithRequest.setId(1);
        itemWithRequest.setName("Drill");
        itemWithRequest.setDescription("Powerful drill");
        itemWithRequest.setAvailable(true);
        itemWithRequest.setOwner(owner);
        itemWithRequest.setRequest(request);  // устанавливаем request

        itemDto.setRequestId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(requestRepository.findById(1)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(itemWithRequest);

        ItemDto result = itemService.create(itemDto, 1);

        assertThat(result).isNotNull();
        assertThat(result.getRequestId()).isEqualTo(1);
    }

    @Test
    void create_shouldThrowNotFoundException_whenRequestNotFound() {
        itemDto.setRequestId(99);

        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(requestRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.create(itemDto, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос не найден");
    }

    @Test
    void create_shouldSaveItemWithoutRequest_whenRequestIdIsNull() {
        itemDto.setRequestId(null);

        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.create(itemDto, 1);

        assertThat(result).isNotNull();
        assertThat(result.getRequestId()).isNull();
    }

    // ========== UPDATE TESTS ==========

    @Test
    void update_shouldUpdateItem_whenUserIsOwner() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Drill");

        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.update(1, updateDto, 1);

        assertThat(result.getName()).isEqualTo("Updated Drill");
    }

    @Test
    void update_shouldThrowException_whenUserIsNotOwner() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.update(1, itemDto, 99))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Редактировать вещь может только владелец");
    }

    @Test
    void update_shouldThrowException_whenItemNotFound() {
        when(itemRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.update(99, itemDto, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь не найдена");
    }

    // ========== GET BY ID TESTS ==========

    @Test
    void getById_shouldReturnItem_whenItemExists() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(1)).thenReturn(List.of());

        ItemDto result = itemService.getById(1, 2);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    void getById_shouldThrowException_whenItemNotFound() {
        when(itemRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getById(99, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь не найдена");
    }

    // ========== GET BY OWNER TESTS ==========

    @Test
    void getByOwner_shouldReturnItems_whenUserExists() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(1)).thenReturn(List.of(item));
        when(commentRepository.findByItemIdIn(any())).thenReturn(List.of());

        List<ItemDto> result = itemService.getByOwner(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Drill");
    }

    // ========== SEARCH TESTS ==========

    @Test
    void search_shouldReturnAvailableItems_whenTextIsNotEmpty() {
        when(itemRepository.searchAvailable("drill")).thenReturn(List.of(item));

        List<ItemDto> result = itemService.search("drill", 1);

        assertThat(result).hasSize(1);
    }

    @Test
    void search_shouldReturnEmptyList_whenTextIsBlank() {
        List<ItemDto> result = itemService.search("", 1);
        assertThat(result).isEmpty();
    }

    // ========== ADD COMMENT TESTS ==========

    @Test
    void addComment_shouldSaveComment_whenUserHasCompletedBooking() {
        CommentCreateDto commentDto = new CommentCreateDto();
        commentDto.setText("Great item!");

        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(userRepository.findById(2)).thenReturn(Optional.of(booker));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                eq(1), eq(2), eq(StatusBooking.APPROVED), any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(1, commentDto, 2);

        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo("Great item!");
    }

    @Test
    void addComment_shouldThrowItemUnavailableException_whenUserHasNoCompletedBooking() {
        // ✅ ИСПРАВЛЕНО: используем ItemUnavailableException вместо ValidationException
        CommentCreateDto commentDto = new CommentCreateDto();
        commentDto.setText("Great item!");

        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(userRepository.findById(2)).thenReturn(Optional.of(booker));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                eq(1), eq(2), eq(StatusBooking.APPROVED), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThatThrownBy(() -> itemService.addComment(1, commentDto, 2))
                .isInstanceOf(ItemUnavailableException.class)
                .hasMessageContaining("Пользователь не брал эту вещь в аренду");
    }
}