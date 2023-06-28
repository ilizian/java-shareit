package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) throws NotFoundException {
        log.info("Запрос пользователя по id " + userId);
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Запрос списка всех пользователей");
        return userService.getUsers();
    }

    @PostMapping
    public UserDto addUser(@RequestBody @Valid UserDto userDto) throws ValidationException {
        log.info("Добавление пользователя");
        return userService.addUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) throws NotFoundException {
        log.info("Удаление пользователя по id " + userId);
        userService.deleteUser(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable long userId) throws NotFoundException,
            ValidationException {
        log.info("Обновление пользователя по id " + userId);
        return userService.updateUser(userDto, userId);
    }
}
