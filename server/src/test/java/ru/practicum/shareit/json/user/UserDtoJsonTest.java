package ru.practicum.shareit.json.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testSerialize() throws Exception {
        UserDto dto = new UserDto(1, "John Doe", "john@example.com");

        JsonContent<UserDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john@example.com");
    }

    @Test
    void testSerializeWithNullFields() throws Exception {
        UserDto dto = new UserDto(null, null, null);

        JsonContent<UserDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");

        assertThat(result).extractingJsonPathNumberValue("$.id").isNull();
        assertThat(result).extractingJsonPathStringValue("$.name").isNull();
        assertThat(result).extractingJsonPathStringValue("$.email").isNull();
    }

    @Test
    void testSerializeWithEmptyName() throws Exception {
        UserDto dto = new UserDto(1, "", "john@example.com");

        JsonContent<UserDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEmpty();
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john@example.com");
    }

    @Test
    void testSerializeWithEmptyEmail() throws Exception {
        UserDto dto = new UserDto(1, "John Doe", "");

        JsonContent<UserDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.email").isEmpty();
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john@example.com\"}";

        UserDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("John Doe");
        assertThat(dto.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testDeserializeWithNullFields() throws Exception {
        String jsonContent = "{\"id\":null,\"name\":null,\"email\":null}";

        UserDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isNull();
    }

    @Test
    void testDeserializeWithoutId() throws Exception {
        String jsonContent = "{\"name\":\"John Doe\",\"email\":\"john@example.com\"}";

        UserDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isEqualTo("John Doe");
        assertThat(dto.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testDeserializeWithoutName() throws Exception {
        String jsonContent = "{\"id\":1,\"email\":\"john@example.com\"}";

        UserDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testDeserializeWithoutEmail() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"John Doe\"}";

        UserDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("John Doe");
        assertThat(dto.getEmail()).isNull();
    }

    @Test
    void testDeserializeWithEmptyFields() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"\",\"email\":\"\"}";

        UserDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEmpty();
        assertThat(dto.getEmail()).isEmpty();
    }

    @Test
    void testDeserializeWithExtraFields() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john@example.com\",\"extraField\":\"ignored\"}";

        UserDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("John Doe");
        assertThat(dto.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testRoundTrip() throws Exception {
        UserDto original = new UserDto(1, "John Doe", "john@example.com");

        String jsonString = json.write(original).getJson();
        UserDto deserialized = json.parse(jsonString).getObject();

        assertThat(deserialized.getId()).isEqualTo(original.getId());
        assertThat(deserialized.getName()).isEqualTo(original.getName());
        assertThat(deserialized.getEmail()).isEqualTo(original.getEmail());
    }

    @Test
    void testRoundTripWithNullFields() throws Exception {
        UserDto original = new UserDto(null, null, null);

        String jsonString = json.write(original).getJson();
        UserDto deserialized = json.parse(jsonString).getObject();

        assertThat(deserialized.getId()).isNull();
        assertThat(deserialized.getName()).isNull();
        assertThat(deserialized.getEmail()).isNull();
    }
}
