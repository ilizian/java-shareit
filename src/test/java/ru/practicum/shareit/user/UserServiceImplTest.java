package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
public class UserServiceImplTest {
    @Mock
    private UserStorage userStorage;
    @InjectMocks
    private UserServiceImpl userService;
    List<User> users = new ArrayList<>();

    @BeforeEach
    void init() {
        User user1 = new User(1L, "user1", "user1@test.ru");
        users.add(user1);
        User user2 = new User(2L, "user2", "user2@test.ru");
        users.add(user2);
    }

    @Test
    void addUserTest() {
        Mockito
                .when(userStorage.save(Mockito.any(User.class)))
                .thenReturn(users.get(0));
        UserDto userDto = UserMapper.toUserDto(users.get(0));
        UserDto userDtoSave = userService.addUser(userDto);
        assertNotNull(userDtoSave);
        assertEquals(users.get(0).getId(), userDtoSave.getId());
        assertEquals(users.get(0).getEmail(), userDtoSave.getEmail());
        assertEquals(users.get(0).getName(), userDtoSave.getName());
    }

    @Test
    void addUserSameEmailErrorTest() {
        User user = new User(1L, "user", "user@test.ru");
        UserDto userDto = UserMapper.toUserDto(user);
        ConflictException exception = assertThrows(ConflictException.class,
                () -> userService.addUser(userDto));
        assertEquals("Ошибка. Такой пользователь уже существует", exception.getMessage());
    }

    @Test
    void updateUserTest() throws NotFoundException {
        User user = new User(1L, "updateUser", "updateUser@test.ru");
        UserDto userDto = UserMapper.toUserDto(user);
        Mockito
                .when(userStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(users.get(0)));

        Mockito
                .when(userStorage.save(Mockito.any(User.class)))
                .thenReturn(user);
        UserDto updatedUser = userService.updateUser(userDto, users.get(0).getId());
        assertEquals(users.get(0).getId(), updatedUser.getId());
        assertEquals(userDto.getName(), updatedUser.getName());
    }

    @Test
    void updateUserIdErrorTest() {
        User user = new User(99L, "updateUser", null);
        UserDto userDto = UserMapper.toUserDto(user);
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> userService.updateUser(userDto, 99L));
        assertThat(e.getMessage(), equalTo("Ошибка. Пользователь не найден с id 99"));
    }

    @Test
    void updateUserSameErrorTest() {
        User user = new User(1L, "user1", "user1@test.ru");
        UserDto userDto = UserMapper.toUserDto(user);
        Mockito
                .when(userStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(users.get(0)));
        ConflictException exception = assertThrows(ConflictException.class,
                () -> userService.updateUser(userDto, users.get(0).getId()));
        assertEquals("Ошибка. Такой пользователь уже существует", exception.getMessage());
    }

    @Test
    void getUserByIdTest() throws NotFoundException {
        Mockito
                .when(userStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(users.get(0)));
        UserDto userDto = userService.getUserById(1L);
        assertNotNull(userDto);
        assertEquals(users.get(0).getId(), userDto.getId());
        assertEquals(users.get(0).getName(), userDto.getName());
    }

    @Test
    void getUsersTest() {
        Mockito
                .when(userStorage.findAll())
                .thenReturn(users);
        Collection<UserDto> usersDto = userService.getUsers();
        assertEquals(users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList()), usersDto);
    }
}
