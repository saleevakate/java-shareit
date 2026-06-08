package ru.practicum.shareit.comment.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentCreateDtoTest {

    @Test
    void noArgsConstructor_shouldCreateEmptyObject() {
        CommentCreateDto dto = new CommentCreateDto();

        assertThat(dto.getText()).isNull();
    }

    @Test
    void allArgsConstructor_shouldSetFields() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Great item!");

        assertThat(dto.getText()).isEqualTo("Great item!");
    }

    @Test
    void setter_shouldUpdateText() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Awesome!");

        assertThat(dto.getText()).isEqualTo("Awesome!");
    }

    @Test
    void getter_shouldReturnText() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Nice!");

        assertThat(dto.getText()).isEqualTo("Nice!");
    }

    @Test
    void equals_shouldReturnTrue_whenSameText() {
        CommentCreateDto dto1 = new CommentCreateDto();
        dto1.setText("Great item!");

        CommentCreateDto dto2 = new CommentCreateDto();
        dto2.setText("Great item!");

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnTrue_whenBothNull() {
        CommentCreateDto dto1 = new CommentCreateDto();
        CommentCreateDto dto2 = new CommentCreateDto();

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentText() {
        CommentCreateDto dto1 = new CommentCreateDto();
        dto1.setText("Great item!");

        CommentCreateDto dto2 = new CommentCreateDto();
        dto2.setText("Different text");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenFirstNullSecondNotNull() {
        CommentCreateDto dto1 = new CommentCreateDto();
        CommentCreateDto dto2 = new CommentCreateDto();
        dto2.setText("Text");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenComparedToNull() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Text");

        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentClass() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Text");

        assertThat(dto).isNotEqualTo(new Object());
    }

    @Test
    void hashCode_shouldBeSame_whenSameText() {
        CommentCreateDto dto1 = new CommentCreateDto();
        dto1.setText("Great item!");

        CommentCreateDto dto2 = new CommentCreateDto();
        dto2.setText("Great item!");

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void hashCode_shouldBeSame_whenBothNull() {
        CommentCreateDto dto1 = new CommentCreateDto();
        CommentCreateDto dto2 = new CommentCreateDto();

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void hashCode_shouldBeDifferent_whenDifferentText() {
        CommentCreateDto dto1 = new CommentCreateDto();
        dto1.setText("Great item!");

        CommentCreateDto dto2 = new CommentCreateDto();
        dto2.setText("Different text");

        assertThat(dto1.hashCode()).isNotEqualTo(dto2.hashCode());
    }

    @Test
    void toString_shouldContainText() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Great item!");

        String result = dto.toString();

        assertThat(result).contains("Great item!");
    }

    @Test
    void toString_shouldHandleNull() {
        CommentCreateDto dto = new CommentCreateDto();

        String result = dto.toString();

        assertThat(result).contains("null");
    }

    @Test
    void canHandleNullText() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText(null);

        assertThat(dto.getText()).isNull();
    }

    @Test
    void canHandleEmptyText() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("");

        assertThat(dto.getText()).isEmpty();
    }

    @Test
    void canHandleLongText() {
        String longText = "Great! ".repeat(100);
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText(longText);

        assertThat(dto.getText()).isEqualTo(longText);
    }
}
