package ru.practicum.shareit.item.service;

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
import ru.practicum.shareit.exception.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

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
    private Booking lastBooking;
    private Booking nextBooking;
    private Comment comment;

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

        request = new ItemRequest();
        request.setId(1);
        request.setDescription("Need a drill");
        request.setRequestor(owner);
        request.setCreated(LocalDateTime.now());

        item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);

        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1);

        LocalDateTime now = LocalDateTime.now();
        lastBooking = new Booking();
        lastBooking.setId(1);
        lastBooking.setStart(now.minusDays(2));
        lastBooking.setEnd(now.minusDays(1));
        lastBooking.setBooker(booker);
        lastBooking.setItem(item);
        lastBooking.setStatus(StatusBooking.APPROVED);

        nextBooking = new Booking();
        nextBooking.setId(2);
        nextBooking.setStart(now.plusDays(1));
        nextBooking.setEnd(now.plusDays(2));
        nextBooking.setBooker(booker);
        nextBooking.setItem(item);
        nextBooking.setStatus(StatusBooking.APPROVED);

        comment = new Comment();
        comment.setId(1);
        comment.setText("Great item!");
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setCreated(now);
    }

    @Test
    void create_shouldSaveItem_whenUserExists() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(requestRepository.findById(1)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.create(itemDto, 1);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.getRequestId()).isEqualTo(1);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void create_shouldSaveItemWithoutRequest_whenRequestIdIsNull() {
        ItemDto dtoWithoutRequest = new ItemDto();
        dtoWithoutRequest.setName("Drill");
        dtoWithoutRequest.setDescription("Powerful drill");
        dtoWithoutRequest.setAvailable(true);
        dtoWithoutRequest.setRequestId(null);

        Item itemWithoutRequest = new Item();
        itemWithoutRequest.setId(1);
        itemWithoutRequest.setName("Drill");
        itemWithoutRequest.setDescription("Powerful drill");
        itemWithoutRequest.setAvailable(true);
        itemWithoutRequest.setOwner(owner);
        itemWithoutRequest.setRequest(null);

        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(itemWithoutRequest);

        ItemDto result = itemService.create(dtoWithoutRequest, 1);

        assertThat(result).isNotNull();
        assertThat(result.getRequestId()).isNull();
        verify(requestRepository, never()).findById(any());
    }

    @Test
    void create_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.create(itemDto, 99))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void create_shouldThrowException_whenRequestNotFound() {
        itemDto.setRequestId(999);
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(requestRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.create(itemDto, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос не найден");
    }

    @Test
    void update_shouldUpdateItem_whenUserIsOwner() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Drill");
        updateDto.setDescription("Updated description");
        updateDto.setAvailable(false);

        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.update(1, updateDto, 1);

        assertThat(result).isNotNull();
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void update_shouldUpdateOnlyName_whenOtherFieldsNull() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Drill");

        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.update(1, updateDto, 1);

        assertThat(result).isNotNull();
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void update_shouldUpdateOnlyDescription_whenOtherFieldsNull() {
        ItemDto updateDto = new ItemDto();
        updateDto.setDescription("Updated description");

        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.update(1, updateDto, 1);

        assertThat(result).isNotNull();
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void update_shouldUpdateOnlyAvailable_whenOtherFieldsNull() {
        ItemDto updateDto = new ItemDto();
        updateDto.setAvailable(false);

        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.update(1, updateDto, 1);

        assertThat(result).isNotNull();
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void update_shouldThrowException_whenItemNotFound() {
        when(itemRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.update(99, itemDto, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь не найдена");
    }

    @Test
    void update_shouldThrowException_whenUserIsNotOwner() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.update(1, itemDto, 99))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Редактировать вещь может только владелец");
    }

    @Test
    void getById_shouldReturnItemWithBookingsAndComments_whenUserIsOwner() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(1)).thenReturn(List.of(comment));
        when(bookingRepository.findLastBooking(eq(1), any(LocalDateTime.class)))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findNextBooking(eq(1), any(LocalDateTime.class)))
                .thenReturn(List.of(nextBooking));

        ItemWithBookingsDto result = (ItemWithBookingsDto) itemService.getById(1, 1);

        assertThat(result).isNotNull();
        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getNextBooking()).isNotNull();
        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    void getById_shouldReturnItemWithoutBookings_whenUserIsOwnerButNoBookings() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(1)).thenReturn(List.of(comment));
        when(bookingRepository.findLastBooking(eq(1), any(LocalDateTime.class)))
                .thenReturn(List.of());
        when(bookingRepository.findNextBooking(eq(1), any(LocalDateTime.class)))
                .thenReturn(List.of());

        ItemWithBookingsDto result = (ItemWithBookingsDto) itemService.getById(1, 1);

        assertThat(result).isNotNull();
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    void getById_shouldReturnItemWithoutBookings_whenUserIsNotOwner() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(1)).thenReturn(List.of(comment));

        ItemWithBookingsDto result = (ItemWithBookingsDto) itemService.getById(1, 99);

        assertThat(result).isNotNull();
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).hasSize(1);
        verify(bookingRepository, never()).findLastBooking(any(), any());
        verify(bookingRepository, never()).findNextBooking(any(), any());
    }

    @Test
    void getById_shouldThrowException_whenItemNotFound() {
        when(itemRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getById(99, 1))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByOwner_shouldReturnItemsWithBookingsAndComments() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(1)).thenReturn(List.of(item));
        when(commentRepository.findByItemIdIn(List.of(1))).thenReturn(List.of(comment));
        when(bookingRepository.findLastBooking(eq(1), any(LocalDateTime.class)))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findNextBooking(eq(1), any(LocalDateTime.class)))
                .thenReturn(List.of(nextBooking));

        List<ItemWithBookingsDto> result = itemService.getByOwner(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastBooking()).isNotNull();
        assertThat(result.get(0).getNextBooking()).isNotNull();
        assertThat(result.get(0).getComments()).hasSize(1);
    }

    @Test
    void getByOwner_shouldReturnEmptyList_whenUserHasNoItems() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(1)).thenReturn(List.of());

        List<ItemWithBookingsDto> result = itemService.getByOwner(1);

        assertThat(result).isEmpty();
    }

    @Test
    void getByOwner_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getByOwner(99))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void search_shouldReturnAvailableItems_whenTextIsNotEmpty() {
        when(itemRepository.searchAvailable("drill")).thenReturn(List.of(item));

        List<ItemDto> result = itemService.search("drill", 1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Drill");
    }

    @Test
    void search_shouldReturnEmptyList_whenTextIsNull() {
        List<ItemDto> result = itemService.search(null, 1);

        assertThat(result).isEmpty();
        verify(itemRepository, never()).searchAvailable(any());
    }

    @Test
    void search_shouldReturnEmptyList_whenTextIsBlank() {
        List<ItemDto> result = itemService.search("   ", 1);

        assertThat(result).isEmpty();
        verify(itemRepository, never()).searchAvailable(any());
    }

    @Test
    void search_shouldReturnEmptyList_whenNoMatches() {
        when(itemRepository.searchAvailable("nonexistent")).thenReturn(List.of());

        List<ItemDto> result = itemService.search("nonexistent", 1);

        assertThat(result).isEmpty();
    }

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
        assertThat(result.getAuthorName()).isEqualTo("Booker");
    }

    @Test
    void addComment_shouldThrowException_whenItemNotFound() {
        CommentCreateDto commentDto = new CommentCreateDto();
        commentDto.setText("Great item!");

        when(itemRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.addComment(99, commentDto, 2))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void addComment_shouldThrowException_whenUserNotFound() {
        CommentCreateDto commentDto = new CommentCreateDto();
        commentDto.setText("Great item!");

        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.addComment(1, commentDto, 99))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void addComment_shouldThrowException_whenUserHasNoCompletedBooking() {
        CommentCreateDto commentDto = new CommentCreateDto();
        commentDto.setText("Great item!");

        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(userRepository.findById(2)).thenReturn(Optional.of(booker));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                eq(1), eq(2), eq(StatusBooking.APPROVED), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThatThrownBy(() -> itemService.addComment(1, commentDto, 2))
                .isInstanceOf(MethodArgumentNotValidException.class)
                .hasMessageContaining("Пользователь не брал эту вещь в аренду");
    }
}
