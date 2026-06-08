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
}