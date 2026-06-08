package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoTest {

    private UserDto dto;

    @BeforeEach
    void setUp() {
        dto = new UserDto();
        dto.setId(1);
        dto.setName("John Doe");
        dto.setEmail("john@example.com");
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyObject() {
        UserDto emptyDto = new UserDto();

        assertThat(emptyDto.getId()).isNull();
        assertThat(emptyDto.getName()).isNull();
        assertThat(emptyDto.getEmail()).isNull();
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        UserDto newDto = new UserDto(1, "Jane Doe", "jane@example.com");

        assertThat(newDto.getId()).isEqualTo(1);
        assertThat(newDto.getName()).isEqualTo("Jane Doe");
        assertThat(newDto.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void setters_shouldUpdateFields() {
        dto.setId(2);
        dto.setName("Jane Doe");
        dto.setEmail("jane@example.com");

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getName()).isEqualTo("Jane Doe");
        assertThat(dto.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void getters_shouldReturnCorrectValues() {
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("John Doe");
        assertThat(dto.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void equals_shouldReturnTrue_whenAllFieldsSame() {
        UserDto dto2 = new UserDto(1, "John Doe", "john@example.com");

        assertThat(dto).isEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentId() {
        UserDto dto2 = new UserDto(2, "John Doe", "john@example.com");

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentName() {
        UserDto dto2 = new UserDto(1, "Jane Doe", "john@example.com");

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentEmail() {
        UserDto dto2 = new UserDto(1, "John Doe", "john2@example.com");

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void hashCode_shouldBeSame_whenAllFieldsSame() {
        UserDto dto2 = new UserDto(1, "John Doe", "john@example.com");

        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void hashCode_shouldBeDifferent_whenDifferentId() {
        UserDto dto2 = new UserDto(2, "John Doe", "john@example.com");

        assertThat(dto.hashCode()).isNotEqualTo(dto2.hashCode());
    }

    @Test
    void hashCode_shouldBeDifferent_whenDifferentName() {
        UserDto dto2 = new UserDto(1, "Jane Doe", "john@example.com");

        assertThat(dto.hashCode()).isNotEqualTo(dto2.hashCode());
    }

    @Test
    void hashCode_shouldBeDifferent_whenDifferentEmail() {
        UserDto dto2 = new UserDto(1, "John Doe", "john2@example.com");

        assertThat(dto.hashCode()).isNotEqualTo(dto2.hashCode());
    }

    @Test
    void toString_shouldContainAllFields() {
        String result = dto.toString();

        assertThat(result).contains("id=1");
        assertThat(result).contains("name=John Doe");
        assertThat(result).contains("email=john@example.com");
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
    void canHandleNullEmail() {
        dto.setEmail(null);

        assertThat(dto.getEmail()).isNull();
    }

    @Test
    void canHandleAllFieldsNull() {
        UserDto emptyDto = new UserDto(null, null, null);

        assertThat(emptyDto.getId()).isNull();
        assertThat(emptyDto.getName()).isNull();
        assertThat(emptyDto.getEmail()).isNull();
    }

    @Test
    void equals_shouldReturnFalse_whenComparedToNull() {
        assertThat(dto.equals(null)).isFalse();
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentClass() {
        assertThat(dto).isNotEqualTo(new Object());
    }

    @Test
    void equals_shouldReturnTrue_whenSameObject() {
        assertThat(dto).isEqualTo(dto);
    }
}
