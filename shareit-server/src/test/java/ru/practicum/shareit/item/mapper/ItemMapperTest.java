package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    private Item item;
    private ItemDto itemDto;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        request = new ItemRequest();
        request.setId(5);

        item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setRequest(request);

        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
        itemDto.setRequestId(5);
    }

    @Test
    void toItemDto_shouldMapCorrectly() {
        ItemDto result = ItemMapper.toItemDto(item);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.getDescription()).isEqualTo("Powerful drill");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isEqualTo(5);
    }

    @Test
    void toItemDto_shouldReturnNull_whenItemIsNull() {
        ItemDto result = ItemMapper.toItemDto(null);
        assertThat(result).isNull();
    }

    @Test
    void toItemDto_shouldHandleItemWithRequest() {
        Item result = ItemMapper.toItem(itemDto);
        assertThat(result.getRequest()).isNull();
    }

    @Test
    void toItemDto_shouldHandleItemWithoutRequest() {
        item.setRequest(null);
        ItemDto result = ItemMapper.toItemDto(item);

        assertThat(result.getRequestId()).isNull();
    }

    @Test
    void toItem_shouldMapCorrectly() {
        Item result = ItemMapper.toItem(itemDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.getDescription()).isEqualTo("Powerful drill");
        assertThat(result.isAvailable()).isTrue();
        assertThat(result.getRequest()).isNull();
    }

    @Test
    void toItem_shouldReturnNull_whenItemDtoIsNull() {
        Item result = ItemMapper.toItem(null);
        assertThat(result).isNull();
    }

    @Test
    void toItem_shouldHandleItemDtoWithRequestId() {
        itemDto.setRequestId(5);
        Item result = ItemMapper.toItem(itemDto);

        assertThat(result.getRequest()).isNull();
    }

    @Test
    void toItem_shouldHandleItemDtoWithoutId() {
        ItemDto dtoWithoutId = new ItemDto();
        dtoWithoutId.setName("New Item");
        dtoWithoutId.setDescription("Description");
        dtoWithoutId.setAvailable(true);

        Item result = ItemMapper.toItem(dtoWithoutId);

        assertThat(result.getId()).isEqualTo(0);
        assertThat(result.getName()).isEqualTo("New Item");
    }

    @Test
    void toItem_shouldHandleNullAvailable() {
        itemDto.setAvailable(null);
        Item result = ItemMapper.toItem(itemDto);

        assertThat(result).isNotNull();
        assertThat(result.isAvailable()).isFalse();
    }

    @Test
    void toItem_shouldHandleFalseAvailable() {
        itemDto.setAvailable(false);
        Item result = ItemMapper.toItem(itemDto);

        assertThat(result).isNotNull();
        assertThat(result.isAvailable()).isFalse();
    }

    @Test
    void toItem_shouldHandleTrueAvailable() {
        itemDto.setAvailable(true);
        Item result = ItemMapper.toItem(itemDto);

        assertThat(result).isNotNull();
        assertThat(result.isAvailable()).isTrue();
    }

    @Test
    void toItem_shouldHandleNullName() {
        itemDto.setName(null);
        Item result = ItemMapper.toItem(itemDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isNull();
    }

    @Test
    void toItem_shouldHandleNullDescription() {
        itemDto.setDescription(null);
        Item result = ItemMapper.toItem(itemDto);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isNull();
    }

    @Test
    void toItem_shouldHandleAllNullFields() {
        ItemDto emptyDto = new ItemDto();
        Item result = ItemMapper.toItem(emptyDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(0);
        assertThat(result.getName()).isNull();
        assertThat(result.getDescription()).isNull();
        assertThat(result.isAvailable()).isFalse();
    }
}
