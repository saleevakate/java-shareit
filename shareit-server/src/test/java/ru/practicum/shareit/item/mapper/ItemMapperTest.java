package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
        itemDto.setRequestId(null);
    }

    @Test
    void toItemDto_shouldMapCorrectly() {
        ItemDto result = ItemMapper.toItemDto(item);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.getDescription()).isEqualTo("Powerful drill");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void toItemDto_shouldReturnNull_whenItemIsNull() {
        ItemDto result = ItemMapper.toItemDto(null);
        assertThat(result).isNull();
    }

    @Test
    void toItem_shouldMapCorrectly() {
        Item result = ItemMapper.toItem(itemDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.getDescription()).isEqualTo("Powerful drill");
        assertThat(result.isAvailable()).isTrue();
    }

    @Test
    void toItem_shouldReturnNull_whenItemDtoIsNull() {
        Item result = ItemMapper.toItem(null);
        assertThat(result).isNull();
    }

    @Test
    void toItem_shouldHandleNullRequestId() {
        itemDto.setRequestId(null);
        Item result = ItemMapper.toItem(itemDto);

        assertThat(result).isNotNull();
        assertThat(result.getRequest()).isNull();
    }
}