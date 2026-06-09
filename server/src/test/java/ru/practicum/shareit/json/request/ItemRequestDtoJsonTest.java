package ru.practicum.shareit.json.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime created = LocalDateTime.now();
        List<ItemResponseDto> items = List.of(
                new ItemResponseDto(1, "Drill", 2),
                new ItemResponseDto(2, "Hammer", 3)
        );
        ItemRequestDto dto = new ItemRequestDto(1, "Need a drill", created, items);

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Need a drill");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(2);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Drill");
    }

    @Test
    void testSerializeWithEmptyItems() throws Exception {
        LocalDateTime created = LocalDateTime.now();
        ItemRequestDto dto = new ItemRequestDto(1, "Need a drill", created, List.of());

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();
    }

    @Test
    void testSerializeWithNullItems() throws Exception {
        LocalDateTime created = LocalDateTime.now();
        ItemRequestDto dto = new ItemRequestDto(1, "Need a drill", created, null);

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathValue("$.items").isNull();
    }

    @Test
    void testSerializeWithNullFields() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(null, null, null, null);

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isNull();
        assertThat(result).extractingJsonPathStringValue("$.description").isNull();
        assertThat(result).extractingJsonPathStringValue("$.created").isNull();
        assertThat(result).extractingJsonPathValue("$.items").isNull();
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{"
                + "\"id\":1,"
                + "\"description\":\"Need a drill\","
                + "\"created\":\"2025-01-01T10:00:00\","
                + "\"items\":["
                + "{\"id\":1,\"name\":\"Drill\",\"ownerId\":2},"
                + "{\"id\":2,\"name\":\"Hammer\",\"ownerId\":3}"
                + "]}";

        ItemRequestDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getCreated()).isNotNull();
        assertThat(dto.getItems()).hasSize(2);
        assertThat(dto.getItems().get(0).getId()).isEqualTo(1);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Drill");
        assertThat(dto.getItems().get(1).getId()).isEqualTo(2);
        assertThat(dto.getItems().get(1).getName()).isEqualTo("Hammer");
    }

    @Test
    void testDeserializeWithEmptyItems() throws Exception {
        String jsonContent = "{"
                + "\"id\":1,"
                + "\"description\":\"Need a drill\","
                + "\"created\":\"2025-01-01T10:00:00\","
                + "\"items\":[]}";

        ItemRequestDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void testDeserializeWithoutItems() throws Exception {
        String jsonContent = "{"
                + "\"id\":1,"
                + "\"description\":\"Need a drill\","
                + "\"created\":\"2025-01-01T10:00:00\"}";

        ItemRequestDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getItems()).isNull();
    }

    @Test
    void testDeserializeWithNullFields() throws Exception {
        String jsonContent = "{\"id\":null,\"description\":null,\"created\":null,\"items\":null}";

        ItemRequestDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getCreated()).isNull();
        assertThat(dto.getItems()).isNull();
    }

    @Test
    void testRoundTrip() throws Exception {
        LocalDateTime created = LocalDateTime.now();
        List<ItemResponseDto> items = List.of(new ItemResponseDto(1, "Drill", 2));
        ItemRequestDto original = new ItemRequestDto(1, "Need a drill", created, items);

        String jsonString = json.write(original).getJson();
        ItemRequestDto deserialized = json.parse(jsonString).getObject();

        assertThat(deserialized.getId()).isEqualTo(original.getId());
        assertThat(deserialized.getDescription()).isEqualTo(original.getDescription());
        assertThat(deserialized.getItems()).hasSize(1);
    }
}
