package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> usersMailSet = new HashSet<>();
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
        checkEmail(user);
        user.setId(generateId());
        usersMailSet.add(user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(User user, long userId) throws NotFoundException {
        if (users.containsKey(user.getId())) {
            User userOld = users.get(userId);
            checkEmail(user);
            if (user.getEmail() == null) {
                user.setEmail(userOld.getEmail());
            }
            if (user.getName() == null) {
                user.setName(userOld.getName());
            }
            updateUsersMailSet(userOld.getEmail(), user.getEmail());
            users.put(user.getId(), user);
            return user;
        }
        throw new NotFoundException("Ошибка. Неправильный id пользователя");
    }

    @Override
    public void deleteUser(long userId) {
        usersMailSet.remove(users.get(userId).getEmail());
        users.remove(userId);
    }

    private void checkUser(User user) throws ValidationException, ConflictException {
        if (user.getName() == null || user.getEmail() == null) {
            throw new ValidationException("Невозможно создать пользователя");
        }
    }

    private void checkEmail(User user) throws ConflictException {
        if (usersMailSet.contains(user.getEmail())) {
            if (!Objects.equals(users.get(user.getId()).getEmail(), user.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже существует");
            }
        }
    }

    private void updateUsersMailSet(String mailOld, String mailNew) {
        if (!mailOld.equals(mailNew)) {
            usersMailSet.remove(mailOld);
            usersMailSet.add(mailNew);
        }
    }

    private long generateId() {
        return ++generateId;
    }
}
