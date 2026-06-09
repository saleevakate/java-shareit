package ru.practicum.shareit.json.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemResponseDtoJsonTest {

    @Autowired
    private JacksonTester<ItemResponseDto> json;

    @Test
    void testSerialize() throws Exception {
        ItemResponseDto dto = new ItemResponseDto(1, "Drill", 2);

        JsonContent<ItemResponseDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.ownerId");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(2);
    }

    @Test
    void testSerializeWithNullFields() throws Exception {
        ItemResponseDto dto = new ItemResponseDto(null, null, null);

        JsonContent<ItemResponseDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isNull();
        assertThat(result).extractingJsonPathStringValue("$.name").isNull();
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isNull();
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"Drill\",\"ownerId\":2}";

        ItemResponseDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getOwnerId()).isEqualTo(2);
    }

    @Test
    void testDeserializeWithNullFields() throws Exception {
        String jsonContent = "{\"id\":null,\"name\":null,\"ownerId\":null}";

        ItemResponseDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getOwnerId()).isNull();
    }

    @Test
    void testDeserializeWithoutOwnerId() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"Drill\"}";

        ItemResponseDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getOwnerId()).isNull();
    }

    @Test
    void testRoundTrip() throws Exception {
        ItemResponseDto original = new ItemResponseDto(1, "Drill", 2);

        String jsonString = json.write(original).getJson();
        ItemResponseDto deserialized = json.parse(jsonString).getObject();

        assertThat(deserialized.getId()).isEqualTo(original.getId());
        assertThat(deserialized.getName()).isEqualTo(original.getName());
        assertThat(deserialized.getOwnerId()).isEqualTo(original.getOwnerId());
    }
}
