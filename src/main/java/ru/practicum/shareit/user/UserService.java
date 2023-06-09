package ru.practicum.shareit.user;

import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(long userId) throws NotFoundException;

    List<UserDto> getUsers();

    UserDto addUser(UserDto userDto) throws ValidationException;

    void deleteUser(long userId);

    UserDto updateUser(UserDto userDto, long userId) throws NotFoundException, ValidationException;

}
