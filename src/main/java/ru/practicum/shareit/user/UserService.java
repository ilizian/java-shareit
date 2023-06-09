package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(long userId) throws NotFoundException;

    List<UserDto> getUsers();

    UserDto addUser(UserDto userDto) throws ValidationException;

    void deleteUser(long userId) throws NotFoundException;

    UserDto updateUser(UserDto userDto, long userId) throws NotFoundException, ValidationException;

}
