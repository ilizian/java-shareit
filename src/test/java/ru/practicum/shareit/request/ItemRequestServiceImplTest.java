package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
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
    @Autowired
    private ItemController itemController;
    private UserDto userDto;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void init() {
        userDto = new UserDto(1L, "user", "user@test.ru");
        itemRequestDto = new ItemRequestDto(1L, "itemReqTest", LocalDateTime.now());
    }

    @Test
    void addItemRequestTest() throws ValidationException, NotFoundException {
        UserDto user = userController.addUser(userDto);
        ItemRequestDtoResponse itemRequest = itemRequestController.addItemRequest(user.getId(), itemRequestDto);
        assertEquals(1L, itemRequestController.getItemRequestById(itemRequest.getId(), user.getId()).getId());
    }

    @Test
    void addItemRequestDescErrorTest() throws ValidationException {
        userController.addUser(userDto);
        itemRequestDto.setDescription(null);
        assertThrows(ValidationException.class, () -> itemRequestController.addItemRequest(1L,
                itemRequestDto));
    }

    @Test
    void addItemRequestErrorTest() {
        assertThrows(NotFoundException.class, () -> itemRequestController.addItemRequest(99L,
                itemRequestDto));
    }

    @Test
    void getByUserTest() throws ValidationException, NotFoundException {
        UserDto user = userController.addUser(userDto);
        itemRequestController.addItemRequest(user.getId(), itemRequestDto);
        assertEquals(1, itemRequestController.getByUser(user.getId()).size());
    }

    @Test
    void getByUserErrorTest() throws ValidationException, NotFoundException {
        UserDto user = userController.addUser(userDto);
        itemRequestController.addItemRequest(user.getId(), itemRequestDto);
        assertThrows(NotFoundException.class, () -> itemRequestController.getByUser(99L));
    }

    @Test
    void getAllTest() throws ValidationException, NotFoundException {
        UserDto user = userController.addUser(userDto);
        itemRequestController.addItemRequest(user.getId(), itemRequestDto);
        assertEquals(0, itemRequestController.getAll(user.getId(), 0, 10).size());
        UserDto user2 = userController.addUser(UserDto.builder().name("user2").email("user2@test.ru").build());
        assertEquals(1, itemRequestController.getAll(user2.getId(), 0, 10).size());
    }

    @Test
    void getAllErrorTest() throws ValidationException, NotFoundException {
        UserDto user = userController.addUser(userDto);
        itemRequestController.addItemRequest(user.getId(), itemRequestDto);
        assertThrows(NotFoundException.class, () -> itemRequestController.getAll(99L, 0, 10));
    }

    @Test
    void getItemRequestByIdTest() throws ValidationException, NotFoundException {
        UserDto user = userController.addUser(userDto);
        ItemRequestDtoResponse itemRequest = itemRequestController.addItemRequest(user.getId(), itemRequestDto);
        assertEquals(1L, itemRequestController.getItemRequestById(itemRequest.getId(), user.getId()).getId());
    }

    @Test
    void getItemRequestByIdErrorIdUserTest() throws ValidationException, NotFoundException {
        UserDto user = userController.addUser(userDto);
        ItemRequestDtoResponse itemRequest = itemRequestController.addItemRequest(user.getId(), itemRequestDto);
        assertThrows(NotFoundException.class, () -> itemRequestController
                .getItemRequestById(itemRequest.getId(), 99L));
    }

    @Test
    void getItemRequestByIdErrorIdItemRequestTest() throws ValidationException, NotFoundException {
        UserDto user = userController.addUser(userDto);
        itemRequestController.addItemRequest(user.getId(), itemRequestDto);
        assertThrows(NotFoundException.class, () -> itemRequestController
                .getItemRequestById(99L, userDto.getId()));
    }

    @Test
    void setItemsInItemRequestsTest() throws ValidationException, NotFoundException {
        UserDto user1 = userController.addUser(userDto);
        ItemDto itemDto1 = new ItemDto(1L, "itemTest1", "itemDescTest1",
                true, null, null, null, 1L);
        ItemDto itemDto2 = new ItemDto(2L, "itemTest2", "itemDescTest2",
                true, null, null, null, 1L);
        itemController.addItem(itemDto1, user1.getId());
        itemController.addItem(itemDto2, user1.getId());
        itemRequestController.addItemRequest(user1.getId(), itemRequestDto);
        assertEquals(itemRequestController.getByUser(user1.getId()).size(), 1);
    }
}