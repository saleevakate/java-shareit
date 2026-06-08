package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestDtoTest {

    private LocalDateTime created;
    private List<ItemResponseDto> items;
    private ItemRequestDto dto;

    @BeforeEach
    void setUp() {
        created = LocalDateTime.now();
        items = new ArrayList<>();
        items.add(new ItemResponseDto(1, "Drill", 2));
        items.add(new ItemResponseDto(2, "Hammer", 3));

        dto = new ItemRequestDto();
        dto.setId(1);
        dto.setDescription("Need a drill");
        dto.setCreated(created);
        dto.setItems(items);
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyObject() {
        ItemRequestDto emptyDto = new ItemRequestDto();

        assertThat(emptyDto.getId()).isNull();
        assertThat(emptyDto.getDescription()).isNull();
        assertThat(emptyDto.getCreated()).isNull();
        assertThat(emptyDto.getItems()).isNull();
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        ItemRequestDto newDto = new ItemRequestDto(1, "Need a drill", created, items);

        assertThat(newDto.getId()).isEqualTo(1);
        assertThat(newDto.getDescription()).isEqualTo("Need a drill");
        assertThat(newDto.getCreated()).isEqualTo(created);
        assertThat(newDto.getItems()).isEqualTo(items);
    }

    @Test
    void setters_shouldUpdateFields() {
        LocalDateTime newCreated = LocalDateTime.now().plusDays(1);
        List<ItemResponseDto> newItems = new ArrayList<>();
        newItems.add(new ItemResponseDto(3, "Screwdriver", 4));

        dto.setId(2);
        dto.setDescription("Need a hammer");
        dto.setCreated(newCreated);
        dto.setItems(newItems);

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getDescription()).isEqualTo("Need a hammer");
        assertThat(dto.getCreated()).isEqualTo(newCreated);
        assertThat(dto.getItems()).isEqualTo(newItems);
    }

    @Test
    void getters_shouldReturnCorrectValues() {
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getCreated()).isEqualTo(created);
        assertThat(dto.getItems()).isEqualTo(items);
    }

    @Test
    void equals_shouldReturnTrue_whenAllFieldsSame() {
        ItemRequestDto dto2 = new ItemRequestDto(1, "Need a drill", created, items);

        assertThat(dto).isEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentId() {
        ItemRequestDto dto2 = new ItemRequestDto(2, "Need a drill", created, items);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentDescription() {
        ItemRequestDto dto2 = new ItemRequestDto(1, "Need a hammer", created, items);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentCreated() {
        ItemRequestDto dto2 = new ItemRequestDto(1, "Need a drill", created.plusDays(1), items);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentItems() {
        List<ItemResponseDto> differentItems = new ArrayList<>();
        differentItems.add(new ItemResponseDto(3, "Screwdriver", 4));

        ItemRequestDto dto2 = new ItemRequestDto(1, "Need a drill", created, differentItems);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void hashCode_shouldBeSame_whenAllFieldsSame() {
        ItemRequestDto dto2 = new ItemRequestDto(1, "Need a drill", created, items);

        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void hashCode_shouldBeDifferent_whenDifferentId() {
        ItemRequestDto dto2 = new ItemRequestDto(2, "Need a drill", created, items);

        assertThat(dto.hashCode()).isNotEqualTo(dto2.hashCode());
    }

    @Test
    void toString_shouldContainFields() {
        String result = dto.toString();

        assertThat(result).contains("id=1");
        assertThat(result).contains("description=Need a drill");
        assertThat(result).contains("created");
        assertThat(result).contains("items");
    }

    @Test
    void canHandleNullItems() {
        dto.setItems(null);

        assertThat(dto.getItems()).isNull();
    }

    @Test
    void canHandleEmptyItems() {
        dto.setItems(new ArrayList<>());

        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void canHandleNullCreated() {
        dto.setCreated(null);

        assertThat(dto.getCreated()).isNull();
    }
}
