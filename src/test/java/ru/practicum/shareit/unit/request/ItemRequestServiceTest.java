package ru.practicum.shareit.unit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
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
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private User user;
    private ItemRequest request;
    private ItemRequestCreateDto createDto;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");

        request = new ItemRequest();
        request.setId(1);
        request.setDescription("Need a drill");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");

        item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(request);
    }

    // ========== CREATE TESTS ==========

    @Test
    void create_shouldSaveRequest_whenUserExists() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto result = requestService.create(createDto, 1);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void create_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.create(createDto, 99))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    // ========== GET BY USER TESTS ==========

    @Test
    void getByUser_shouldReturnUserRequests_whenUserExists() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequestorId(eq(1), any(Sort.class))).thenReturn(List.of(request));
        when(itemRepository.findByRequestIdIn(any())).thenReturn(List.of());

        List<ItemRequestDto> result = requestService.getByUser(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
    }

    @Test
    void getByUser_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.getByUser(99))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByUser_shouldReturnEmptyList_whenNoRequests() {
        // ✅ ИСПРАВЛЕНО: убрали лишний мок itemRepository.findByRequestIdIn
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequestorId(eq(1), any(Sort.class))).thenReturn(List.of());

        List<ItemRequestDto> result = requestService.getByUser(1);

        assertThat(result).isEmpty();
    }

    // ========== GET ALL OTHER TESTS ==========

    @Test
    void getAllOther_shouldReturnOtherUsersRequests() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequestorIdNot(eq(1), any(Sort.class))).thenReturn(List.of(request));
        when(itemRepository.findByRequestIdIn(any())).thenReturn(List.of());

        List<ItemRequestDto> result = requestService.getAllOther(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllOther_shouldReturnEmptyList_whenNoOtherRequests() {
        // ✅ ИСПРАВЛЕНО: убрали лишний мок itemRepository.findByRequestIdIn
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequestorIdNot(eq(1), any(Sort.class))).thenReturn(List.of());

        List<ItemRequestDto> result = requestService.getAllOther(1);

        assertThat(result).isEmpty();
    }

    // ========== GET BY ID TESTS ==========

    @Test
    void getById_shouldReturnRequest_whenRequestExists() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(1)).thenReturn(List.of());

        ItemRequestDto result = requestService.getById(1, 1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void getById_shouldReturnRequestWithItems_whenItemsExist() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(1)).thenReturn(List.of(item));

        ItemRequestDto result = requestService.getById(1, 1);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getId()).isEqualTo(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Drill");
    }

    @Test
    void getById_shouldThrowNotFoundException_whenRequestNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.getById(99, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос не найден");
    }

    @Test
    void getById_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.getById(1, 99))
                .isInstanceOf(NotFoundException.class);
    }
}