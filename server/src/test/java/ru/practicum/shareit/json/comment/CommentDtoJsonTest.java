package ru.practicum.shareit.json.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime created = LocalDateTime.now();
        CommentDto dto = new CommentDto(1, "Great item!", "User", created);

        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Great item!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("User");
    }

    @Test
    void testSerializeWithNullFields() throws Exception {
        CommentDto dto = new CommentDto(null, null, null, null);

        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");

        assertThat(result).extractingJsonPathNumberValue("$.id").isNull();
        assertThat(result).extractingJsonPathStringValue("$.text").isNull();
        assertThat(result).extractingJsonPathStringValue("$.authorName").isNull();
        assertThat(result).extractingJsonPathStringValue("$.created").isNull();
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{\"id\":1,\"text\":\"Great item!\",\"authorName\":\"User\",\"created\":\"2025-01-01T10:00:00\"}";

        CommentDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getText()).isEqualTo("Great item!");
        assertThat(dto.getAuthorName()).isEqualTo("User");
        assertThat(dto.getCreated()).isNotNull();
    }

    @Test
    void testDeserializeWithNullFields() throws Exception {
        String jsonContent = "{\"id\":null,\"text\":null,\"authorName\":null,\"created\":null}";

        CommentDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getText()).isNull();
        assertThat(dto.getAuthorName()).isNull();
        assertThat(dto.getCreated()).isNull();
    }

    @Test
    void testDeserializeWithEmptyString() throws Exception {
        String jsonContent = "{\"id\":1,\"text\":\"\",\"authorName\":\"\",\"created\":\"2025-01-01T10:00:00\"}";

        CommentDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getText()).isEmpty();
        assertThat(dto.getAuthorName()).isEmpty();
        assertThat(dto.getCreated()).isNotNull();
    }
}
