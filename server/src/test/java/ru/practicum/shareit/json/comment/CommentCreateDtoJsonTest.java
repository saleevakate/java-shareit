package ru.practicum.shareit.json.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.dto.CommentCreateDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentCreateDtoJsonTest {

    @Autowired
    private JacksonTester<CommentCreateDto> json;

    @Test
    void testSerialize() throws Exception {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Great item!");

        JsonContent<CommentCreateDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Great item!");
    }

    @Test
    void testSerializeWithEmptyText() throws Exception {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("");

        JsonContent<CommentCreateDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEmpty();
    }

    @Test
    void testSerializeWithNullText() throws Exception {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText(null);

        JsonContent<CommentCreateDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isNull();
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{\"text\":\"Great item!\"}";

        CommentCreateDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getText()).isEqualTo("Great item!");
    }

    @Test
    void testDeserializeWithEmptyText() throws Exception {
        String jsonContent = "{\"text\":\"\"}";

        CommentCreateDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getText()).isEmpty();
    }

    @Test
    void testDeserializeWithNullText() throws Exception {
        String jsonContent = "{\"text\":null}";

        CommentCreateDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getText()).isNull();
    }
}
