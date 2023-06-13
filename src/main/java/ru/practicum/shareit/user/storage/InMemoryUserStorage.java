package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.ConflictException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long generateId = 0;

    @Override
    public User getUser(long userId) throws NotFoundException {
        if (users.containsKey(userId)) {
            return users.get(userId);
        }
        throw new NotFoundException("Ошибка. Невозможно получить пользователя с id " + userId);
    }

    @Override
    public User addUser(User user) throws ValidationException {
        checkUser(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(User user, long userId) throws NotFoundException {
        checkEmail(user, userId);
        if (users.containsKey(user.getId())) {
            User userOld = users.get(userId);
            if (user.getEmail() == null) {
                user.setEmail(userOld.getEmail());
            }
            if (user.getName() == null) {
                user.setName(userOld.getName());
            }
            users.put(user.getId(), user);
            return user;
        }
        throw new NotFoundException("Ошибка. Неправильный id пользователя");
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
    }

    private void checkUser(User user) throws ValidationException, ConflictException {
        if (user.getName() == null | user.getEmail() == null) {
            throw new ValidationException("Невозможно создать пользователя");
        }
        for (User userExist : users.values()) {
            if (Objects.equals(user.getEmail(), userExist.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже создан");
            }
        }
    }

    private void checkEmail(User user, long userId) throws ConflictException {
        for (User userExist : users.values()) {
            if (!Objects.equals(userExist.getId(), userId)) {
                if (Objects.equals(userExist.getEmail(), user.getEmail())) {
                    throw new ConflictException("Пользователь с таким email уже существует");
                }
            }
        }
    }

    private long generateId() {
        return ++generateId;
    }
}
