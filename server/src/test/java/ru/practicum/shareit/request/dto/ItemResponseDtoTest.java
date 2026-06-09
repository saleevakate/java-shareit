package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemResponseDtoTest {

    private ItemResponseDto dto;

    @BeforeEach
    void setUp() {
        dto = new ItemResponseDto();
        dto.setId(1);
        dto.setName("Drill");
        dto.setOwnerId(2);
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyObject() {
        ItemResponseDto emptyDto = new ItemResponseDto();

        assertThat(emptyDto.getId()).isNull();
        assertThat(emptyDto.getName()).isNull();
        assertThat(emptyDto.getOwnerId()).isNull();
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        ItemResponseDto newDto = new ItemResponseDto(1, "Drill", 2);

        assertThat(newDto.getId()).isEqualTo(1);
        assertThat(newDto.getName()).isEqualTo("Drill");
        assertThat(newDto.getOwnerId()).isEqualTo(2);
    }

    @Test
    void setters_shouldUpdateFields() {
        dto.setId(3);
        dto.setName("Hammer");
        dto.setOwnerId(4);

        assertThat(dto.getId()).isEqualTo(3);
        assertThat(dto.getName()).isEqualTo("Hammer");
        assertThat(dto.getOwnerId()).isEqualTo(4);
    }

    @Test
    void getters_shouldReturnCorrectValues() {
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getOwnerId()).isEqualTo(2);
    }

    @Test
    void equals_shouldReturnTrue_whenAllFieldsSame() {
        ItemResponseDto dto2 = new ItemResponseDto(1, "Drill", 2);

        assertThat(dto).isEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentId() {
        ItemResponseDto dto2 = new ItemResponseDto(2, "Drill", 2);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentName() {
        ItemResponseDto dto2 = new ItemResponseDto(1, "Hammer", 2);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentOwnerId() {
        ItemResponseDto dto2 = new ItemResponseDto(1, "Drill", 3);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void hashCode_shouldBeSame_whenAllFieldsSame() {
        ItemResponseDto dto2 = new ItemResponseDto(1, "Drill", 2);

        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void hashCode_shouldBeDifferent_whenDifferentId() {
        ItemResponseDto dto2 = new ItemResponseDto(2, "Drill", 2);

        assertThat(dto.hashCode()).isNotEqualTo(dto2.hashCode());
    }

    @Test
    void toString_shouldContainFields() {
        String result = dto.toString();

        assertThat(result).contains("id=1");
        assertThat(result).contains("name=Drill");
        assertThat(result).contains("ownerId=2");
    }

    @Test
    void canHandleNullId() {
        dto.setId(null);

        assertThat(dto.getId()).isNull();
    }

    @Test
    void canHandleNullName() {
        dto.setName(null);

        assertThat(dto.getName()).isNull();
    }

    @Test
    void canHandleNullOwnerId() {
        dto.setOwnerId(null);

        assertThat(dto.getOwnerId()).isNull();
    }
}
