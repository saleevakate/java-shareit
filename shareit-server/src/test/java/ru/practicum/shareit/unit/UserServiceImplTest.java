package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

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
    void create_shouldSaveUser_whenEmailIsUnique() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.create(userDto);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void create_shouldThrowException_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThatThrownBy(() -> userService.create(userDto))
                .isInstanceOf(ru.practicum.shareit.exception.ValidationException.class)
                .hasMessageContaining("Email уже существует");
    }

    @Test
    void getById_shouldReturnUser_whenUserExists() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        UserDto result = userService.getById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    void getById_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99))
                .isInstanceOf(ru.practicum.shareit.exception.NotFoundException.class);
    }

    @Test
    void getAll_shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void update_shouldUpdateUser_whenUserExists() {
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.update(1, updateDto);

        assertThat(result.getName()).isEqualTo("Updated Name");
    }

    @Test
    void delete_shouldDeleteUser() {
        userService.delete(1);
        verify(userRepository, times(1)).deleteById(1);
    }
}