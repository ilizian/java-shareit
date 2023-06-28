package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto getUserById(long userId) throws NotFoundException {
        try {
            return UserMapper.toUserDto(userStorage.getReferenceById(userId));
        } catch (Exception ex) {
            throw new NotFoundException("Ошибка. Не найден пользователь");
        }
    }

    @Override
    public List<UserDto> getUsers() {
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        try {
            return UserMapper.toUserDto(userStorage.save(user));
        } catch (RuntimeException ex) {
            throw new ConflictException("Ошибка. Такой пользователь уже существует");
        }
    }

    @Override
    public void deleteUser(long userId) throws NotFoundException {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Ошибка. Не найден пользователь");
        }
        userStorage.deleteById(userId);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) throws NotFoundException {
        Optional<User> userOptional = userStorage.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Ошибка. Пользователь не найден с id " + userId);
        }
        User user = userOptional.get();
        if (Objects.nonNull(userDto.getEmail())) {
            user.setEmail(userDto.getEmail());
        }
        if (Objects.nonNull(userDto.getName())) {
            user.setName(userDto.getName());
        }
        try {
            return UserMapper.toUserDto(userStorage.save(user));
        } catch (RuntimeException ex) {
            throw new ConflictException("Ошибка. Такой пользователь уже существует");
        }
    }
}
