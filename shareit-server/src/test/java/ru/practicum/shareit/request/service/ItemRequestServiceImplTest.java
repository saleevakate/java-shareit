package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

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

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Test User");

        request = new ItemRequest();
        request.setId(1);
        request.setDescription("Need a drill");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");
    }

    @Test
    void create_shouldSaveRequest_whenUserExists() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto result = requestService.create(createDto, 1);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void getByUser_shouldReturnRequests() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequestorId(eq(1), any(Sort.class))).thenReturn(List.of(request));
        when(itemRepository.findByRequestIdIn(any())).thenReturn(List.of());

        List<ItemRequestDto> result = requestService.getByUser(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void getById_shouldReturnRequest() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(1)).thenReturn(List.of());

        ItemRequestDto result = requestService.getById(1, 1);

        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    void getById_shouldThrowException_whenRequestNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(requestRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.getById(99, 1))
                .isInstanceOf(NotFoundException.class);
    }
}