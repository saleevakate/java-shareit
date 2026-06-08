package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");

        userDto = new UserDto();
        userDto.setId(1);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
    }

    @Test
    void toUserDto_shouldMapCorrectly() {
        UserDto result = UserMapper.toUserDto(user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void toUserDto_shouldReturnNull_whenUserIsNull() {
        UserDto result = UserMapper.toUserDto(null);
        assertThat(result).isNull();
    }

    @Test
    void toUser_shouldMapCorrectly() {
        User result = UserMapper.toUser(userDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void toUser_shouldReturnNull_whenUserDtoIsNull() {
        User result = UserMapper.toUser(null);
        assertThat(result).isNull();
    }

    @Test
    void toUser_shouldHandleUserDtoWithoutId() {
        UserDto dtoWithoutId = new UserDto();
        dtoWithoutId.setName("No Id User");
        dtoWithoutId.setEmail("noid@example.com");

        User result = UserMapper.toUser(dtoWithoutId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(0);
        assertThat(result.getName()).isEqualTo("No Id User");
        assertThat(result.getEmail()).isEqualTo("noid@example.com");
    }

    @Test
    void toUser_shouldHandleNullName() {
        UserDto dtoWithNullName = new UserDto();
        dtoWithNullName.setId(1);
        dtoWithNullName.setName(null);
        dtoWithNullName.setEmail("test@example.com");

        User result = UserMapper.toUser(dtoWithNullName);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void toUser_shouldHandleNullEmail() {
        UserDto dtoWithNullEmail = new UserDto();
        dtoWithNullEmail.setId(1);
        dtoWithNullEmail.setName("Test User");
        dtoWithNullEmail.setEmail(null);

        User result = UserMapper.toUser(dtoWithNullEmail);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isNull();
    }

    @Test
    void toUserDto_shouldHandleUserWithNullFields() {
        user.setName(null);
        user.setEmail(null);

        UserDto result = UserMapper.toUserDto(user);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isNull();
        assertThat(result.getEmail()).isNull();
    }

    @Test
    void bidirectionalMapping_shouldBeConsistent() {
        UserDto mappedDto = UserMapper.toUserDto(user);
        User mappedUser = UserMapper.toUser(mappedDto);

        assertThat(mappedUser.getId()).isEqualTo(user.getId());
        assertThat(mappedUser.getName()).isEqualTo(user.getName());
        assertThat(mappedUser.getEmail()).isEqualTo(user.getEmail());
    }
}
