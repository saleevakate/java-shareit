package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestCreateDtoTest {

    private ItemRequestCreateDto dto;

    @BeforeEach
    void setUp() {
        dto = new ItemRequestCreateDto();
        dto.setDescription("Need a drill");
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyObject() {
        ItemRequestCreateDto emptyDto = new ItemRequestCreateDto();

        assertThat(emptyDto.getDescription()).isNull();
    }

    @Test
    void setter_shouldUpdateDescription() {
        dto.setDescription("Need a hammer");

        assertThat(dto.getDescription()).isEqualTo("Need a hammer");
    }

    @Test
    void getter_shouldReturnDescription() {
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void equals_shouldReturnTrue_whenSameDescription() {
        ItemRequestCreateDto dto2 = new ItemRequestCreateDto();
        dto2.setDescription("Need a drill");

        assertThat(dto).isEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentDescription() {
        ItemRequestCreateDto dto2 = new ItemRequestCreateDto();
        dto2.setDescription("Need a hammer");

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void hashCode_shouldBeSame_whenSameDescription() {
        ItemRequestCreateDto dto2 = new ItemRequestCreateDto();
        dto2.setDescription("Need a drill");

        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void hashCode_shouldBeDifferent_whenDifferentDescription() {
        ItemRequestCreateDto dto2 = new ItemRequestCreateDto();
        dto2.setDescription("Need a hammer");

        assertThat(dto.hashCode()).isNotEqualTo(dto2.hashCode());
    }

    @Test
    void toString_shouldContainDescription() {
        String result = dto.toString();

        assertThat(result).contains("Need a drill");
    }

    @Test
    void canHandleNullDescription() {
        dto.setDescription(null);

        assertThat(dto.getDescription()).isNull();
    }
}
