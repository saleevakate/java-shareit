package ru.practicum.shareit.json.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestCreateDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestCreateDto> json;

    @Test
    void testSerialize() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a drill");

        JsonContent<ItemRequestCreateDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Need a drill");
    }

    @Test
    void testSerializeWithNullDescription() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription(null);

        JsonContent<ItemRequestCreateDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isNull();
    }

    @Test
    void testSerializeWithEmptyDescription() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("");

        JsonContent<ItemRequestCreateDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEmpty();
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{\"description\":\"Need a drill\"}";

        ItemRequestCreateDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void testDeserializeWithNullDescription() throws Exception {
        String jsonContent = "{\"description\":null}";

        ItemRequestCreateDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getDescription()).isNull();
    }

    @Test
    void testDeserializeWithEmptyDescription() throws Exception {
        String jsonContent = "{\"description\":\"\"}";

        ItemRequestCreateDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getDescription()).isEmpty();
    }

    @Test
    void testRoundTrip() throws Exception {
        ItemRequestCreateDto original = new ItemRequestCreateDto();
        original.setDescription("Need a drill");

        String jsonString = json.write(original).getJson();
        ItemRequestCreateDto deserialized = json.parse(jsonString).getObject();

        assertThat(deserialized.getDescription()).isEqualTo(original.getDescription());
    }
}
