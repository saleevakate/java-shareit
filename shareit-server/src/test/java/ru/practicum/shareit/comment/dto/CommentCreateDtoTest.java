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
    void equals_shouldReturnTrue_whenSameText() {
        CommentCreateDto dto1 = new CommentCreateDto();
        dto1.setText("Great item!");

        CommentCreateDto dto2 = new CommentCreateDto();
        dto2.setText("Great item!");

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
    void hashCode_shouldBeSame_whenSameText() {
        CommentCreateDto dto1 = new CommentCreateDto();
        dto1.setText("Great item!");

        CommentCreateDto dto2 = new CommentCreateDto();
        dto2.setText("Great item!");

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void toString_shouldContainText() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Great item!");

        String result = dto.toString();

        assertThat(result).contains("Great item!");
    }
}