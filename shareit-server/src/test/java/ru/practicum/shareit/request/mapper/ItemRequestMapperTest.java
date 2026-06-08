package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

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

        createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");

        request = new ItemRequest();
        request.setId(1);
        request.setDescription("Need a drill");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(user);
    }

    @Test
    void toItemRequest_shouldMapCorrectly() {
        ItemRequest result = ItemRequestMapper.toItemRequest(createDto, user);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Need a drill");
        assertThat(result.getRequestor()).isEqualTo(user);
        assertThat(result.getCreated()).isNotNull();
    }

    @Test
    void toItemRequestDto_shouldMapCorrectly() {
        List<ItemResponseDto> items = List.of(ItemRequestMapper.toItemResponseDto(item));
        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(request, items);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getDescription()).isEqualTo("Need a drill");
        assertThat(result.getCreated()).isEqualTo(request.getCreated());
        assertThat(result.getItems()).hasSize(1);
    }

    @Test
    void toItemRequestDto_shouldReturnNull_whenRequestIsNull() {
        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(null, List.of());
        assertThat(result).isNull();
    }

    @Test
    void toItemResponseDto_shouldMapCorrectly() {
        ItemResponseDto result = ItemRequestMapper.toItemResponseDto(item);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.getOwnerId()).isEqualTo(1);
    }

    @Test
    void toItemResponseDto_shouldReturnNull_whenItemIsNull() {
        ItemResponseDto result = ItemRequestMapper.toItemResponseDto(null);
        assertThat(result).isNull();
    }

    @Test
    void toItemRequestDto_shouldHandleNullItems() {
        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(request, null);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getItems()).isNull();
    }

    @Test
    void toItemRequestDto_shouldHandleEmptyItems() {
        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(request, List.of());

        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEmpty();
    }
}
