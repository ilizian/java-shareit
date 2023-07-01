package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.misc.Status;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class ItemServiceImplTest {
    @Autowired
    private BookingController bookingController;
    @Autowired
    private ItemController itemController;
    @Autowired
    private UserController userController;
    private UserDto userDto;
    private ItemDto itemDto;


    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "user", "user@test.ru");
        itemDto = new ItemDto(1L, "itemTest", "itemDescTest",
                true, null, null, null, null);
    }

    @Test
    void addItemTest() throws ValidationException, NotFoundException {
        userController.addUser(userDto);
        itemController.addItem(itemDto, 1L);
        assertNotNull(itemDto);
        assertEquals(itemDto.getId(), itemController.getItemById(userDto.getId(), itemDto.getId()).getId());
    }

    @Test
    void addItemErrorUserTest() throws ValidationException {
        userController.addUser(userDto);
        assertThrows(NotFoundException.class, () -> itemController.addItem(itemDto, 99L));
    }

    @Test
    void addItemErrorNameTest() throws ValidationException {
        userController.addUser(userDto);
        itemDto.setName(null);
        assertThrows(ConstraintViolationException.class, () -> itemController.addItem(itemDto, 1L));
    }

    @Test
    void addItemErrorDescTest() throws ValidationException {
        userController.addUser(userDto);
        itemDto.setDescription(null);
        assertThrows(ConstraintViolationException.class, () -> itemController.addItem(itemDto, 1L));
    }

    @Test
    void addItemErrorReqTest() throws ValidationException {
        userController.addUser(userDto);
        itemDto.setRequestId(99L);
        assertThrows(NotFoundException.class, () -> itemController.addItem(itemDto, 1L));
    }

    @Test
    void updateItemTest() throws NotFoundException, ValidationException {
        userController.addUser(userDto);
        itemController.addItem(itemDto, 1L);
        ItemDto itemDto1 = ItemDto.builder()
                .name("update")
                .description("update")
                .available(true)
                .build();
        ItemDto itemDtoResult = itemController.updateItem(itemDto.getId(), itemDto1, 1L);
        assertNotNull(itemDtoResult);
        assertEquals(itemDtoResult.getName(), itemDto1.getName());
        assertEquals(itemDtoResult.getDescription(), itemDto1.getDescription());
    }

    @Test
    void updateItemErrorUserTest() {
        ItemDto itemDto1 = ItemDto.builder()
                .name("update")
                .description("update")
                .available(true)
                .build();
        assertThrows(NotFoundException.class,
                () -> itemController.updateItem(itemDto.getId(), itemDto1, 99L));
    }

    @Test
    void updateItemErrorItemTest() {
        ItemDto itemDto1 = ItemDto.builder()
                .name("update")
                .description("update")
                .available(true)
                .build();
        assertThrows(NotFoundException.class,
                () -> itemController.updateItem(99L, itemDto1, 99L));
    }

    @Test
    void getItemByUserTest() throws ValidationException, NotFoundException {
        userController.addUser(userDto);
        itemController.addItem(itemDto, 1L);
        ItemDto itemDto1 = ItemDto.builder()
                .name("update")
                .description("update")
                .available(true)
                .build();
        itemController.addItem(itemDto1, 1L);
        List<ItemDto> result = itemController.getItemByUser(userDto.getId(), 0, 1);
        assertEquals(result.size(), 1);

    }

    @Test
    void getItemByUserErrorUserTest() throws ValidationException, NotFoundException {
        userController.addUser(userDto);
        itemController.addItem(itemDto, 1L);
        ItemDto itemDto1 = ItemDto.builder()
                .name("update")
                .description("update")
                .available(true)
                .build();
        itemController.addItem(itemDto1, 1L);
        assertThrows(NotFoundException.class, () -> itemController.getItemByUser(99L, 0, 1));
    }

    @Test
    void getItemByIdTest() throws ValidationException, NotFoundException {
        userController.addUser(userDto);
        itemController.addItem(itemDto, 1L);
        assertEquals(itemDto.getId(), itemController.getItemById(userDto.getId(), itemDto.getId()).getId());
    }

    @Test
    void getItemByIdErrorTest() throws ValidationException, NotFoundException {
        userController.addUser(userDto);
        itemController.addItem(itemDto, 1L);
        assertThrows(NotFoundException.class, () -> itemController.getItemById(userDto.getId(), 99L));
    }

    @Test
    void search() throws ValidationException, NotFoundException {
        userController.addUser(userDto);
        itemController.addItem(itemDto, 1L);
        assertEquals(itemController.searchItems("itemDescTest", 0, 1).size(), 1);
    }

    @Test
    void addCommentErrorBookerTest() throws ValidationException, NotFoundException {
        UserDto userDto1 = new UserDto(2L, "user1", "user1@test.ru");
        ItemDto itemDto1 = new ItemDto(2L, "itemTest1", "itemDescTest1",
                true, null, null, null, null);
        userController.addUser(userDto);
        itemController.addItem(itemDto, 1L);
        userController.addUser(userDto1);
        itemController.addItem(itemDto1, 2L);
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setItem(ItemMapper.toItem(itemDto, UserMapper.toUser(userDto)));
        booking1.setBooker(UserMapper.toUser(userDto1));
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now().plusHours(1));
        booking1.setEnd(LocalDateTime.now().plusHours(5));
        assertThrows(NotFoundException.class, () -> bookingController.createBooking(userDto.getId(),
                BookingMapper.toBookingDto(booking1)));
    }

    @Test
    void addCommentErrorBookingTest() throws ValidationException, NotFoundException {
        userController.addUser(userDto);
        itemController.addItem(itemDto, 1L);
        CommentDto commentDto = CommentDto.builder()
                .created(LocalDateTime.now())
                .text("text")
                .build();
        assertThrows(ValidationException.class, () -> itemController.addComment(itemDto.getId(),
                userDto.getId(), commentDto));
    }
}