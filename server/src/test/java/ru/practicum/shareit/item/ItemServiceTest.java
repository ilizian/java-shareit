package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.misc.Status;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final UserDto userDto = new UserDto(1L, "user", "user@test.ru");
    private final ItemDto itemDto = new ItemDto(1L, "itemTest", "itemDescTest",
            true, null, null, null, null);


    @Test
    void addItemTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto);
        itemService.addItem(itemDto, 1L);
        assertNotNull(itemDto);
        assertEquals(itemDto.getId(), itemService.getItem(itemDto.getId(), userDto.getId()).getId());
    }

    @Test
    void addItemErrorTest() throws ValidationException {
        userService.addUser(userDto);
        assertThrows(NotFoundException.class, () -> itemService.addItem(itemDto, 99L));
    }

    @Test
    void addItemErrorRequestorTest() throws ValidationException {
        userService.addUser(userDto);
        itemDto.setRequestId(99L);
        assertThrows(NotFoundException.class, () -> itemService.addItem(itemDto, userDto.getId()));
    }

    @Test
    void getItemErrorTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto);
        itemService.addItem(itemDto, 1L);
        assertThrows(NotFoundException.class, () -> itemService.getItem(99L, userDto.getId()));
    }

    @Test
    void updateItemTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto);
        itemService.addItem(itemDto, 1L);
        ItemDto itemDtoUpdate = new ItemDto(1L, "Update", "Update",
                true, null, null, null, null);
        itemService.updateItem(itemDto.getId(), itemDtoUpdate, userDto.getId());
        assertEquals(itemDtoUpdate.getId(), itemService.getItem(itemDto.getId(), userDto.getId()).getId());
        assertEquals(itemDtoUpdate.getDescription(), itemService.getItem(itemDto.getId(), userDto.getId()).getDescription());
    }

    @Test
    void updateItemErrorUserTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto);
        itemService.addItem(itemDto, 1L);
        UserDto userDto2 = new UserDto(2L, "user2", "user2@test.ru");
        userService.addUser(userDto2);
        ItemDto itemDtoUpdate = new ItemDto(1L, "Update", "Update",
                true, null, null, null, null);
        assertThrows(NotFoundException.class, () ->
                itemService.updateItem(itemDto.getId(), itemDtoUpdate, userDto2.getId()));
    }

    @Test
    void updateItemNotNullTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto);
        itemService.addItem(itemDto, 1L);
        ItemDto itemDtoUpdate = new ItemDto(1L, null, null,
                null, null, null, null, null);
        ItemDto itemDtosave = itemService.updateItem(itemDto.getId(), itemDtoUpdate, userDto.getId());
        assertEquals(itemDto.getDescription(), itemDtosave.getDescription());
        assertEquals(itemDto.getAvailable(), itemDtosave.getAvailable());
    }

    @Test
    void searchItemsTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto);
        itemService.addItem(itemDto, 1L);
        ItemDto itemDto1 = new ItemDto(2L, "itemTest2", "itemDescTest2",
                true, null, null, null, null);
        itemService.addItem(itemDto1, 1L);
        assertEquals(2, itemService.searchItems("itemTest", 0, 10).size());
    }

    @Test
    void searchItemsErrorTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto);
        itemService.addItem(itemDto, 1L);
        ItemDto itemDto1 = new ItemDto(2L, "itemTest2", "itemDescTest2",
                true, null, null, null, null);
        itemService.addItem(itemDto1, 1L);
        assertThrows(NotFoundException.class, () ->
                itemService.searchItems("not", 0, 10));
    }

    @Test
    void searchItemsErrorTextTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto);
        itemService.addItem(itemDto, 1L);
        ItemDto itemDto1 = new ItemDto(2L, "itemTest2", "itemDescTest2",
                true, null, null, null, null);
        itemService.addItem(itemDto1, 1L);
        assertEquals(0, itemService.searchItems("", 0, 10).size());
    }
    @Test
    void getItemByUserTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto);
        itemService.addItem(itemDto, 1L);
        ItemDto itemDto1 = new ItemDto(2L, "itemTest2", "itemDescTest2",
                true, null, null, null, null);
        itemService.addItem(itemDto1, 1L);
        assertEquals(2, itemService.getItemByUser(userDto.getId(), 0, 10).size());
    }

    @Test
    void getItemByUserErrorTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto);
        itemService.addItem(itemDto, 1L);
        ItemDto itemDto1 = new ItemDto(2L, "itemTest2", "itemDescTest2",
                true, null, null, null, null);
        itemService.addItem(itemDto1, 1L);
        assertThrows(NotFoundException.class, () ->
                itemService.getItemByUser(99L, 0, 10));
    }

    @Test
    void addCommentTest() throws ValidationException, NotFoundException, InterruptedException {
        userService.addUser(userDto);
        UserDto userDto2 = new UserDto(2L, "user2", "user2@test.ru");
        userService.addUser(userDto2);
        ItemDto itemDto1 = new ItemDto(2L, "itemTest1", "itemDescTest1",
                true, null, null, null, null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().plusNanos(200000000), LocalDateTime.now().plusNanos(300000000),
                ItemMapper.toItem(itemDto, UserMapper.toUser(userDto2)), UserMapper.toUser(userDto), Status.APPROVED);
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(22),
                ItemMapper.toItem(itemDto, UserMapper.toUser(userDto2)), UserMapper.toUser(userDto), Status.WAITING);
        itemDto1.setLastBooking(BookingMapper.toBookingDto(booking1));
        itemDto1.setNextBooking(BookingMapper.toBookingDto(booking2));
        itemService.addItem(itemDto, 1L);
        itemService.addItem(itemDto1, 2L);
        bookingService.createBooking(userDto2.getId(), BookingMapper.toBookingDto(booking1));
        bookingService.createBooking(userDto2.getId(), BookingMapper.toBookingDto(booking2));
        bookingService.updateBooking(1L, 2L, true);
        Thread.sleep(3000);
        CommentDto commentDto = new CommentDto(1L, "text", "Author", LocalDateTime.now());
        CommentDto commentDto1 = itemService.addComment(2L, 1L, commentDto);
        assertNotNull(commentDto1);
    }
}
