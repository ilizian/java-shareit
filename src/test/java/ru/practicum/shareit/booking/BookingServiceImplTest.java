package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.misc.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplTest {
    @Autowired
    private BookingController bookingController;
    @Autowired
    private ItemController itemController;
    @Autowired
    private UserController userController;
    private BookingDto bookingDto;
    private UserDto userDto1;
    private UserDto userDto2;
    private ItemDto itemDto;

    @BeforeEach
    void init() {
        bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 1L, 1L);
        userDto1 = new UserDto(1L, "user1", "user1@test.ru");
        userDto2 = new UserDto(2L, "user2", "user2@test.ru");
        itemDto = new ItemDto(1L, "itemTest", "descTest",
                true, null, null, null, null);
    }

    @Test
    void createBookingTest() throws ValidationException, NotFoundException {
        userController.addUser(userDto1);
        userController.addUser(userDto2);
        itemController.addItem(itemDto, 1L);
        BookingDtoResponse bookingDtoResponse1 = bookingController.createBooking(2L, bookingDto);
        assertEquals(bookingController.getBooking(bookingDtoResponse1.getId(), userDto2.getId()).getId(), 1L);
    }

    @Test
    void createBookingWhenOwnItemTest() throws ValidationException, NotFoundException {
        UserDto userDto = new UserDto(1L, "user", "user@test.ru");
        userController.addUser(userDto);
        itemController.addItem(itemDto, userDto.getId());
        assertThrows(NotFoundException.class, () -> bookingController.createBooking(1L, bookingDto));
    }

    @Test
    void createBookingErrorTest() {
        assertThrows(NotFoundException.class,
                () -> bookingController.createBooking(1L, bookingDto));
    }

    @Test
    void createBookingErrorItemTest() throws ValidationException, NotFoundException {
        BookingDto bookingDtoForException = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(1L)
                .bookerId(1L)
                .build();
        UserDto userDtoNew = userController.addUser(userDto1);
        UserDto userDtoNew2 = userController.addUser(userDto2);
        itemController.addItem(itemDto, userDtoNew.getId());
        assertThrows(ValidationException.class,
                () -> bookingController.createBooking(userDtoNew2.getId(), bookingDtoForException));
    }

    @Test
    void updateBookingTest() throws NotFoundException, ValidationException {
        userController.addUser(userDto1);
        userController.addUser(userDto2);
        itemController.addItem(itemDto, userDto1.getId());
        BookingDtoResponse bookingDtoResponse1 = bookingController.createBooking(userDto2.getId(), bookingDto);
        assertEquals(bookingController.getBooking(bookingDtoResponse1.getId(),
                userDto2.getId()).getStatus(), Status.WAITING);
        bookingController.updateBooking(userDto1.getId(), true, bookingDtoResponse1.getId());
        assertEquals(bookingController.getBooking(bookingDtoResponse1.getId(),
                userDto2.getId()).getStatus(), Status.APPROVED);
    }

    @Test
    void updateBookingErrorTest() {
        assertThrows(NotFoundException.class,
                () -> bookingController.updateBooking(1L, true, 1L));
    }

    @Test
    void getBookingTest() throws NotFoundException, ValidationException {
        userController.addUser(userDto1);
        userController.addUser(userDto2);
        itemController.addItem(itemDto, userDto1.getId());
        BookingDtoResponse bookingDtoResponse1 = bookingController.createBooking(userDto2.getId(), bookingDto);
        assertEquals(bookingController.getBooking(bookingDtoResponse1.getId(), userDto2.getId()).getId(), 1L);
    }
}
