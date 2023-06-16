package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User getUser(long userId) throws NotFoundException;

    User addUser(User user) throws ValidationException;

    List<User> getUsers();

    User updateUser(User user, long userId) throws NotFoundException, ValidationException;

    void deleteUser(long userId);
}
