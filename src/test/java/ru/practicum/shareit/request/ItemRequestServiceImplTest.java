package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ItemRequestServiceImplTest {
    @Autowired
    private UserController userController;
    @Autowired
    private ItemRequestController itemRequestController;
    private UserDto userDto;
    private ItemRequestDtoResponse itemRequestDtoResponse;

    @BeforeEach
    void init() {
        userDto = new UserDto(1L, "user", "user@test.ru");
        itemRequestDtoResponse = new ItemRequestDtoResponse(1L, "itemReqTest",
                LocalDateTime.now(), null);
    }

    @Test
    void addItemRequestTest() throws ValidationException, NotFoundException {
        UserDto user = userController.addUser(userDto);
        ItemRequestDtoResponse itemRequest = itemRequestController.addItemRequest(user.getId(), itemRequestDtoResponse);
        assertEquals(1L, itemRequestController.getItemRequestById(itemRequest.getId(), user.getId()).getId());
    }

    @Test
    void addItemRequestDescErrorTest() throws ValidationException {
        userController.addUser(userDto);
        itemRequestDtoResponse.setDescription(null);
        assertThrows(ValidationException.class, () -> itemRequestController.addItemRequest(1L,
                itemRequestDtoResponse));
    }

    @Test
    void addItemRequestErrorTest() {
        assertThrows(NotFoundException.class, () -> itemRequestController.addItemRequest(99L,
                itemRequestDtoResponse));
    }

    @Test
    void getByUserTest() throws ValidationException, NotFoundException {
        UserDto user = userController.addUser(userDto);
        itemRequestController.addItemRequest(user.getId(), itemRequestDtoResponse);
        assertEquals(1, itemRequestController.getByUser(user.getId()).size());
    }

    @Test
    void getByUserErrorTest() throws ValidationException, NotFoundException {
        UserDto user = userController.addUser(userDto);
        itemRequestController.addItemRequest(user.getId(), itemRequestDtoResponse);
        assertThrows(NotFoundException.class, () -> itemRequestController.getByUser(99L));
    }

    @Test
    void getAllTest() throws ValidationException, NotFoundException {
        UserDto user = userController.addUser(userDto);
        itemRequestController.addItemRequest(user.getId(), itemRequestDtoResponse);
        assertEquals(0, itemRequestController.getAll(user.getId(), 0, 10).size());
        UserDto user2 = userController.addUser(UserDto.builder().name("user2").email("user2@test.ru").build());
        assertEquals(1, itemRequestController.getAll(user2.getId(), 0, 10).size());
    }

    @Test
    void getAllErrorTest() throws ValidationException, NotFoundException {
        UserDto user = userController.addUser(userDto);
        itemRequestController.addItemRequest(user.getId(), itemRequestDtoResponse);
        assertThrows(NotFoundException.class, () -> itemRequestController.getAll(99L, 0, 10));
    }

    @Test
    void getItemRequestByIdTest() throws ValidationException, NotFoundException {
        UserDto user = userController.addUser(userDto);
        ItemRequestDtoResponse itemRequest = itemRequestController.addItemRequest(user.getId(), itemRequestDtoResponse);
        assertEquals(1L, itemRequestController.getItemRequestById(itemRequest.getId(), user.getId()).getId());
    }

    @Test
    void getItemRequestByIdErrorIdUserTest() throws ValidationException, NotFoundException {
        UserDto user = userController.addUser(userDto);
        ItemRequestDtoResponse itemRequest = itemRequestController.addItemRequest(user.getId(), itemRequestDtoResponse);
        assertThrows(NotFoundException.class, () -> itemRequestController
                .getItemRequestById(itemRequest.getId(), 99L));
    }

    @Test
    void getItemRequestByIdErrorIdItemRequestTest() throws ValidationException, NotFoundException {
        UserDto user = userController.addUser(userDto);
        itemRequestController.addItemRequest(user.getId(), itemRequestDtoResponse);
        assertThrows(NotFoundException.class, () -> itemRequestController
                .getItemRequestById(99L, userDto.getId()));
    }
}