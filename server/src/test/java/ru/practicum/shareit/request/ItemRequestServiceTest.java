package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "itemReqTest", LocalDateTime.now());
    private final ItemRequestDto itemRequestDto1 = new ItemRequestDto(2L, "itemReqTest1", LocalDateTime.now());
    private final UserDto userDto = new UserDto(1L, "user", "user@test.ru");

    @Test
    void addItemRequestTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto);
        assertNotNull(userService.getUserById(userDto.getId()));
        itemRequestService.addItemRequest(1L, itemRequestDto);
        assertEquals(itemRequestDto.getId(), itemRequestService.getItemRequestById(1L, 1L).getId());
        assertEquals(itemRequestDto.getDescription(),
                itemRequestService.getItemRequestById(1L, 1L).getDescription());
    }

    @Test
    void addItemRequestErrorTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto);
        assertNotNull(userService.getUserById(userDto.getId()));
        itemRequestDto.setDescription(null);
        assertThrows(ValidationException.class, () -> itemRequestService.addItemRequest(1L, itemRequestDto));
    }

    @Test
    void addItemRequestErrorUserTest() throws ValidationException {
        userService.addUser(userDto);
        assertThrows(NotFoundException.class, () -> itemRequestService.addItemRequest(99L, itemRequestDto));
    }

    @Test
    void getItemRequestErrorTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto);
        itemRequestService.addItemRequest(1L, itemRequestDto);
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getItemRequestById(99L, userDto.getId()));
    }

    @Test
    void getItemRequestErrorUserTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto);
        itemRequestService.addItemRequest(1L, itemRequestDto);
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getItemRequestById(itemRequestDto.getId(), 99L));
    }

    @Test
    void getByUserTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto);
        itemRequestService.addItemRequest(1L, itemRequestDto);
        itemRequestService.addItemRequest(1L, itemRequestDto1);
        assertEquals(2, itemRequestService.getByUser(1L).size());
    }

    @Test
    void getByUserErrorUserTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto);
        itemRequestService.addItemRequest(1L, itemRequestDto);
        itemRequestService.addItemRequest(1L, itemRequestDto1);
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getByUser(99L));
    }

    @Test
    void getAllUserTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto);
        itemRequestService.addItemRequest(1L, itemRequestDto);
        UserDto userDto2 = new UserDto(2L, "user2", "user2@test.ru");
        userService.addUser(userDto2);
        itemRequestService.addItemRequest(2L, itemRequestDto1);
        assertEquals(1, itemRequestService.getAll(2L, 0, 100).size());
    }

    @Test
    void getAllUserErrorUserTest() throws ValidationException, NotFoundException {
        itemRequestService.addItemRequest(1L, itemRequestDto);
        itemRequestService.addItemRequest(1L, itemRequestDto1);
        userService.addUser(userDto);
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getAll(99L, 0, 100));
    }

    @Test
    void setItemsTest() throws ValidationException, NotFoundException {
        ItemDto itemDto1 = new ItemDto(1L, "itemTest1", "itemDescTest1",
                true, null, null, null, 1L);
        ItemDto itemDto2 = new ItemDto(2L, "itemTest2", "itemDescTest2",
                true, null, null, null, 1L);
        userService.addUser(userDto);
        itemService.addItem(itemDto1, userDto.getId());
        itemService.addItem(itemDto2, userDto.getId());
        itemRequestService.addItemRequest(userDto.getId(), itemRequestDto);
        assertEquals(1, itemRequestService.getByUser(1L).size());
        assertEquals(itemDto1.getDescription(),
                itemRequestService.getByUser(1L).get(0).getItems().get(0).getDescription());
    }
}