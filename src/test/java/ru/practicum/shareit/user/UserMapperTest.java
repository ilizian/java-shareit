package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoBooking;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserMapperTest {

    @Test
    void toUserTest() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@test.ru")
                .build();
        User user = UserMapper.toUser(userDto);
        assertNotNull(user);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void toUserDtoTest() {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@test.ru")
                .build();
        UserDto userDto = UserMapper.toUserDto(user);
        assertNotNull(userDto);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void toUserDtoBookingTest() {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@test.ru")
                .build();
        UserDtoBooking userDtoBooking = UserMapper.toUserDtoBooking(user);
        assertNotNull(userDtoBooking);
        assertEquals(userDtoBooking.getId(), user.getId());
        assertEquals(userDtoBooking.getName(), user.getName());
    }
}
