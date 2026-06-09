package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
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
                .isInstanceOf(ValidationException.class)
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
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAll_shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> result = userService.getAll();

        assertThat(result).isEmpty();
    }

    @Test
    void update_shouldUpdateUser_whenUserExistsAndOnlyNameProvided() {
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.update(1, updateDto);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_shouldUpdateUser_whenOnlyEmailProvidedAndEmailNotTaken() {
        UserDto updateDto = new UserDto();
        updateDto.setEmail("newemail@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.update(1, updateDto);

        assertThat(result.getEmail()).isEqualTo("newemail@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_shouldNotUpdateEmail_whenEmailSameAsCurrent() {
        UserDto updateDto = new UserDto();
        updateDto.setEmail("test@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.update(1, updateDto);

        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).save(any(User.class));
        verify(userRepository, times(1)).existsByEmail("test@example.com");
    }

    @Test
    void update_shouldThrowException_whenEmailAlreadyExists() {
        UserDto updateDto = new UserDto();
        updateDto.setEmail("existing@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.update(1, updateDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Email уже существует");
    }

    @Test
    void update_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99, userDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void update_shouldUpdateBothNameAndEmail_whenBothProvided() {
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail("newemail@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.update(1, updateDto);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("newemail@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_shouldNotUpdateAnything_whenDtoHasNullFields() {
        UserDto updateDto = new UserDto();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.update(1, updateDto);

        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void delete_shouldDeleteUser() {
        userService.delete(1);
        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_shouldNotThrowException_whenUserNotFound() {
        doNothing().when(userRepository).deleteById(99);
        userService.delete(99);
        verify(userRepository).deleteById(99);
    }
}
