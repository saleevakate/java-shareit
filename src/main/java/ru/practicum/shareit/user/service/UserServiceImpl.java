package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto create(UserDto userDto) {
        if (userStorage.existsByEmail(userDto.getEmail())) {
            throw new ValidationException("Email уже существует");
        }
        User user = UserMapper.toUser(userDto);
        user = userStorage.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(int id, UserDto userDto) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (userStorage.existsByEmail(userDto.getEmail()) &&
                    !user.getEmail().equals(userDto.getEmail())) {
                throw new ValidationException("Email уже существует");
            }
            user.setEmail(userDto.getEmail());
        }

        user = userStorage.update(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getById(int id) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public void delete(int id) {
        userStorage.deleteById(id);
    }
}