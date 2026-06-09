package ru.practicum.shareit.json.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerialize() throws Exception {
        ItemDto dto = new ItemDto(1, "Drill", "Powerful drill", true, 5);

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Powerful drill");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(5);
    }

    @Test
    void testSerializeWithNullRequestId() throws Exception {
        ItemDto dto = new ItemDto(1, "Drill", "Powerful drill", true, null);

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isNull();
    }

    @Test
    void testSerializeWithNullAvailable() throws Exception {
        ItemDto dto = new ItemDto(1, "Drill", "Powerful drill", null, 5);

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.available");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isNull();
    }

    @Test
    void testSerializeAllNullFields() throws Exception {
        ItemDto dto = new ItemDto(null, null, null, null, null);

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isNull();
        assertThat(result).extractingJsonPathStringValue("$.name").isNull();
        assertThat(result).extractingJsonPathStringValue("$.description").isNull();
        assertThat(result).extractingJsonPathBooleanValue("$.available").isNull();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isNull();
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"Drill\",\"description\":\"Powerful drill\",\"available\":true,\"requestId\":5}";

        ItemDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getDescription()).isEqualTo("Powerful drill");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(5);
    }

    @Test
    void testDeserializeWithoutRequestId() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"Drill\",\"description\":\"Powerful drill\",\"available\":true}";

        ItemDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getDescription()).isEqualTo("Powerful drill");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isNull();
    }

    @Test
    void testDeserializeWithNullFields() throws Exception {
        String jsonContent = "{\"id\":null,\"name\":null,\"description\":null,\"available\":null,\"requestId\":null}";

        ItemDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getAvailable()).isNull();
        assertThat(dto.getRequestId()).isNull();
    }

    @Test
    void testRoundTrip() throws Exception {
        ItemDto original = new ItemDto(1, "Drill", "Powerful drill", true, 5);

        String jsonString = json.write(original).getJson();
        ItemDto deserialized = json.parse(jsonString).getObject();

        assertThat(deserialized.getId()).isEqualTo(original.getId());
        assertThat(deserialized.getName()).isEqualTo(original.getName());
        assertThat(deserialized.getDescription()).isEqualTo(original.getDescription());
        assertThat(deserialized.getAvailable()).isEqualTo(original.getAvailable());
        assertThat(deserialized.getRequestId()).isEqualTo(original.getRequestId());
    }
}
