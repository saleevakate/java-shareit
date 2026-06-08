package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemDtoServerTest {

    private ItemDto dto;

    @BeforeEach
    void setUp() {
        dto = new ItemDto();
        dto.setId(1);
        dto.setName("Drill");
        dto.setDescription("Powerful drill");
        dto.setAvailable(true);
        dto.setRequestId(5);
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyObject() {
        ItemDto emptyDto = new ItemDto();

        assertThat(emptyDto.getId()).isNull();
        assertThat(emptyDto.getName()).isNull();
        assertThat(emptyDto.getDescription()).isNull();
        assertThat(emptyDto.getAvailable()).isNull();
        assertThat(emptyDto.getRequestId()).isNull();
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        ItemDto newDto = new ItemDto(1, "Drill", "Powerful drill", true, 5);

        assertThat(newDto.getId()).isEqualTo(1);
        assertThat(newDto.getName()).isEqualTo("Drill");
        assertThat(newDto.getDescription()).isEqualTo("Powerful drill");
        assertThat(newDto.getAvailable()).isTrue();
        assertThat(newDto.getRequestId()).isEqualTo(5);
    }

    @Test
    void setters_shouldUpdateFields() {
        dto.setId(2);
        dto.setName("Hammer");
        dto.setDescription("Heavy hammer");
        dto.setAvailable(false);
        dto.setRequestId(null);

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getName()).isEqualTo("Hammer");
        assertThat(dto.getDescription()).isEqualTo("Heavy hammer");
        assertThat(dto.getAvailable()).isFalse();
        assertThat(dto.getRequestId()).isNull();
    }

    @Test
    void equals_shouldReturnTrue_whenAllFieldsSame() {
        ItemDto dto2 = new ItemDto(1, "Drill", "Powerful drill", true, 5);

        assertThat(dto).isEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentId() {
        ItemDto dto2 = new ItemDto(2, "Drill", "Powerful drill", true, 5);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentName() {
        ItemDto dto2 = new ItemDto(1, "Hammer", "Powerful drill", true, 5);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentDescription() {
        ItemDto dto2 = new ItemDto(1, "Drill", "Different description", true, 5);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentAvailable() {
        ItemDto dto2 = new ItemDto(1, "Drill", "Powerful drill", false, 5);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentRequestId() {
        ItemDto dto2 = new ItemDto(1, "Drill", "Powerful drill", true, 10);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void hashCode_shouldBeSame_whenAllFieldsSame() {
        ItemDto dto2 = new ItemDto(1, "Drill", "Powerful drill", true, 5);

        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void hashCode_shouldBeDifferent_whenDifferentId() {
        ItemDto dto2 = new ItemDto(2, "Drill", "Powerful drill", true, 5);

        assertThat(dto.hashCode()).isNotEqualTo(dto2.hashCode());
    }

    @Test
    void toString_shouldContainFields() {
        String result = dto.toString();

        assertThat(result).contains("id=1");
        assertThat(result).contains("name=Drill");
        assertThat(result).contains("description=Powerful drill");
        assertThat(result).contains("available=true");
        assertThat(result).contains("requestId=5");
    }

    @Test
    void canHandleNullRequestId() {
        dto.setRequestId(null);

        assertThat(dto.getRequestId()).isNull();
    }

    @Test
    void canHandleNullAvailable() {
        dto.setAvailable(null);

        assertThat(dto.getAvailable()).isNull();
    }
}