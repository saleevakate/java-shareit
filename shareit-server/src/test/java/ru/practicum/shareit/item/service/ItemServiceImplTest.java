package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
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

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1);
        owner.setName("Owner");

        booker = new User();
        booker.setId(2);
        booker.setName("Booker");

        item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);

        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
    }

    @Test
    void create_shouldSaveItem_whenUserExists() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.create(itemDto, 1);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Drill");
    }

    @Test
    void create_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.create(itemDto, 99))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getById_shouldReturnItemWithBookings_whenUserIsOwner() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(1)).thenReturn(List.of());

        ItemDto result = itemService.getById(1, 1);

        assertThat(result).isInstanceOf(ItemWithBookingsDto.class);
    }

    @Test
    void getById_shouldReturnPlainItem_whenUserIsNotOwner() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(1)).thenReturn(List.of());

        ItemWithBookingsDto result = (ItemWithBookingsDto) itemService.getById(1, 99);

        assertThat(result).isNotNull();
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).isNotNull();
    }

    @Test
    void getByOwner_shouldReturnItems() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(1)).thenReturn(List.of(item));
        when(commentRepository.findByItemIdIn(any())).thenReturn(List.of());

        List<ItemWithBookingsDto> result = itemService.getByOwner(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void search_shouldReturnAvailableItems() {
        when(itemRepository.searchAvailable("drill")).thenReturn(List.of(item));

        List<ItemDto> result = itemService.search("drill", 1);

        assertThat(result).hasSize(1);
    }

    @Test
    void addComment_shouldSaveComment_whenUserHasCompletedBooking() {
        CommentCreateDto commentDto = new CommentCreateDto();
        commentDto.setText("Great item!");

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("Great item!");
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setCreated(LocalDateTime.now());

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
}