package ru.practicum.shareit.comment.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentDtoTest {

    private LocalDateTime created;
    private CommentDto dto;

    @BeforeEach
    void setUp() {
        created = LocalDateTime.now();
        dto = new CommentDto(1, "Great item!", "User", created);
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyObject() {
        CommentDto emptyDto = new CommentDto();

        assertThat(emptyDto.getId()).isNull();
        assertThat(emptyDto.getText()).isNull();
        assertThat(emptyDto.getAuthorName()).isNull();
        assertThat(emptyDto.getCreated()).isNull();
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getText()).isEqualTo("Great item!");
        assertThat(dto.getAuthorName()).isEqualTo("User");
        assertThat(dto.getCreated()).isEqualTo(created);
    }

    @Test
    void setters_shouldUpdateFields() {
        LocalDateTime newCreated = LocalDateTime.now().plusDays(1);

        dto.setId(2);
        dto.setText("Awesome!");
        dto.setAuthorName("New User");
        dto.setCreated(newCreated);

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getText()).isEqualTo("Awesome!");
        assertThat(dto.getAuthorName()).isEqualTo("New User");
        assertThat(dto.getCreated()).isEqualTo(newCreated);
    }

    @Test
    void getters_shouldReturnCorrectValues() {
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getText()).isEqualTo("Great item!");
        assertThat(dto.getAuthorName()).isEqualTo("User");
        assertThat(dto.getCreated()).isEqualTo(created);
    }

    @Test
    void equals_shouldReturnTrue_whenAllFieldsSame() {
        CommentDto dto2 = new CommentDto(1, "Great item!", "User", created);

        assertThat(dto).isEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnTrue_whenSameObject() {
        assertThat(dto).isEqualTo(dto);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentId() {
        CommentDto dto2 = new CommentDto(2, "Great item!", "User", created);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentText() {
        CommentDto dto2 = new CommentDto(1, "Different text", "User", created);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentAuthorName() {
        CommentDto dto2 = new CommentDto(1, "Great item!", "Different User", created);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentCreated() {
        CommentDto dto2 = new CommentDto(1, "Great item!", "User", created.plusDays(1));

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenComparedToNull() {
        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentClass() {
        assertThat(dto).isNotEqualTo(new Object());
    }

    @Test
    void hashCode_shouldBeSame_whenAllFieldsSame() {
        CommentDto dto2 = new CommentDto(1, "Great item!", "User", created);

        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void hashCode_shouldBeDifferent_whenDifferentId() {
        CommentDto dto2 = new CommentDto(2, "Great item!", "User", created);

        assertThat(dto.hashCode()).isNotEqualTo(dto2.hashCode());
    }

    @Test
    void hashCode_shouldBeDifferent_whenDifferentText() {
        CommentDto dto2 = new CommentDto(1, "Different text", "User", created);

        assertThat(dto.hashCode()).isNotEqualTo(dto2.hashCode());
    }

    @Test
    void toString_shouldContainFields() {
        String result = dto.toString();

        assertThat(result).contains("id=1");
        assertThat(result).contains("text=Great item!");
        assertThat(result).contains("authorName=User");
        assertThat(result).contains("created");
    }

    @Test
    void canHandleNullId() {
        dto.setId(null);
        assertThat(dto.getId()).isNull();
    }

    @Test
    void canHandleNullText() {
        dto.setText(null);
        assertThat(dto.getText()).isNull();
    }

    @Test
    void canHandleNullAuthorName() {
        dto.setAuthorName(null);
        assertThat(dto.getAuthorName()).isNull();
    }

    @Test
    void canHandleNullCreated() {
        dto.setCreated(null);
        assertThat(dto.getCreated()).isNull();
    }

    @Test
    void canHandleAllFieldsNull() {
        CommentDto emptyDto = new CommentDto(null, null, null, null);

        assertThat(emptyDto.getId()).isNull();
        assertThat(emptyDto.getText()).isNull();
        assertThat(emptyDto.getAuthorName()).isNull();
        assertThat(emptyDto.getCreated()).isNull();
    }
}
