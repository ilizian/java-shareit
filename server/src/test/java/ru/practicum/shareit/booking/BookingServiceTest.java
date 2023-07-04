package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.misc.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    private final UserDto userDto1 = new UserDto(1L, "user", "user@test.ru");
    private final UserDto userDto2 = new UserDto(2L, "user2", "user2@test.ru");
    private final ItemDto itemDto = new ItemDto(1L, "itemTest", "itemDescTest",
            true, null, null, null, null);
    private final BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2), 1L, 1L);

    @Test
    void createBookingTest() throws NotFoundException, ValidationException {
        userService.addUser(userDto1);
        userService.addUser(userDto2);
        itemService.addItem(itemDto, 1L);
        BookingDtoResponse bookingDtoResponse1 = bookingService.createBooking(2L, bookingDto);
        assertEquals(bookingDto.getBookerId(), bookingDtoResponse1.getId());
    }

    @Test
    void createBookingErrorUserTest() throws NotFoundException, ValidationException {
        userService.addUser(userDto1);
        userService.addUser(userDto2);
        itemService.addItem(itemDto, 1L);
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(99L, bookingDto));
    }

    @Test
    void createBookingErrorItemTest() throws NotFoundException, ValidationException {
        userService.addUser(userDto1);
        userService.addUser(userDto2);
        itemService.addItem(itemDto, 1L);
        bookingDto.setItemId(99L);
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(userDto2.getId(), bookingDto));
    }

    @Test
    void createBookingErrorItemNotAvailableTest() throws NotFoundException, ValidationException {
        userService.addUser(userDto1);
        userService.addUser(userDto2);
        itemDto.setAvailable(false);
        itemService.addItem(itemDto, 1L);
        assertThrows(ValidationException.class, () -> bookingService.createBooking(userDto2.getId(), bookingDto));
    }

    @Test
    void createBookingErrorUserOwnerTest() throws NotFoundException, ValidationException {
        userService.addUser(userDto1);
        userService.addUser(userDto2);
        itemService.addItem(itemDto, 1L);
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(userDto1.getId(), bookingDto));
    }

    @Test
    void createBookingErrorStartTest() throws NotFoundException, ValidationException {
        userService.addUser(userDto1);
        userService.addUser(userDto2);
        itemService.addItem(itemDto, 1L);
        bookingDto.setStart(null);
        assertThrows(ValidationException.class, () -> bookingService.createBooking(userDto2.getId(), bookingDto));
    }

    @Test
    void createBookingErrorStartAfterEndTest() throws NotFoundException, ValidationException {
        userService.addUser(userDto1);
        userService.addUser(userDto2);
        itemService.addItem(itemDto, 1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(3));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(userDto2.getId(), bookingDto));
    }

    @Test
    void updateBookingTest() throws NotFoundException, ValidationException {
        userService.addUser(userDto1);
        userService.addUser(userDto2);
        itemService.addItem(itemDto, 1L);
        BookingDtoResponse bookingDtoResponse1 = bookingService.createBooking(2L, bookingDto);
        assertEquals(bookingService.getBooking(bookingDtoResponse1.getId(),
                userDto2.getId()).getStatus(), Status.WAITING);
        bookingService.updateBooking(userDto1.getId(), bookingDtoResponse1.getId(), true);
        assertEquals(bookingService.getBooking(bookingDtoResponse1.getId(),
                userDto2.getId()).getStatus(), Status.APPROVED);
    }

    @Test
    void updateBookingErrorTest() throws NotFoundException, ValidationException {
        userService.addUser(userDto1);
        userService.addUser(userDto2);
        itemService.addItem(itemDto, 1L);
        BookingDtoResponse bookingDtoResponse1 = bookingService.createBooking(2L, bookingDto);
        assertEquals(bookingService.getBooking(bookingDtoResponse1.getId(),
                userDto2.getId()).getStatus(), Status.WAITING);
        assertThrows(NotFoundException.class, () ->
                bookingService.updateBooking(userDto1.getId(), 99L, true));
    }

    @Test
    void updateBookingErrorUserTest() throws NotFoundException, ValidationException {
        userService.addUser(userDto1);
        userService.addUser(userDto2);
        itemService.addItem(itemDto, 1L);
        BookingDtoResponse bookingDtoResponse1 = bookingService.createBooking(2L, bookingDto);
        assertEquals(bookingService.getBooking(bookingDtoResponse1.getId(),
                userDto2.getId()).getStatus(), Status.WAITING);
        assertThrows(NotFoundException.class, () ->
                bookingService.updateBooking(userDto2.getId(), bookingDtoResponse1.getId(), true));
    }

    @Test
    void updateBookingAppTest() throws NotFoundException, ValidationException {
        userService.addUser(userDto1);
        userService.addUser(userDto2);
        itemService.addItem(itemDto, 1L);
        BookingDtoResponse bookingDtoResponse1 = bookingService.createBooking(2L, bookingDto);
        assertEquals(bookingService.getBooking(bookingDtoResponse1.getId(),
                userDto2.getId()).getStatus(), Status.WAITING);
        bookingService.updateBooking(userDto1.getId(), bookingDtoResponse1.getId(), false);
        assertEquals(bookingService.getBooking(bookingDtoResponse1.getId(),
                userDto2.getId()).getStatus(), Status.REJECTED);
    }

    @Test
    void getBookingsOfUserTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto1);
        userService.addUser(userDto2);
        itemService.addItem(itemDto, userDto1.getId());
        bookingService.createBooking(userDto2.getId(), bookingDto);
        assertEquals(bookingService.getBookingsOfUser(userDto2.getId(), "CURRENT", 0, 10).size(), 0);
        assertEquals(bookingService.getBookingsOfUser(userDto2.getId(), "ALL", 0, 10).size(), 2);
        assertEquals(bookingService.getBookingsOfUser(userDto2.getId(), "PAST", 0, 10).size(), 0);
        assertEquals(bookingService.getBookingsOfUser(userDto2.getId(), "WAITING", 0, 10).size(), 1);
        assertEquals(bookingService.getBookingsOfUser(userDto2.getId(), "FUTURE", 0, 10).size(), 1);
        assertEquals(bookingService.getBookingsOfUser(userDto2.getId(), "REJECTED", 0, 10).size(), 0);
    }

    @Test
    void getBookingsOfUserErrorTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto1);
        userService.addUser(userDto2);
        itemService.addItem(itemDto, 1L);
        bookingService.createBooking(2L, bookingDto);
        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsOfUser(99L, "ALL", 0, 10));
    }

    @Test
    void getOwnerItemsAllTest() throws ValidationException, NotFoundException {
        userService.addUser(userDto1);
        userService.addUser(userDto2);
        itemService.addItem(itemDto, userDto1.getId());
        bookingService.createBooking(userDto2.getId(), bookingDto);
        assertEquals(bookingService.getForOwner(userDto1.getId(), "CURRENT", 0, 10).size(), 0);
        assertEquals(bookingService.getForOwner(userDto1.getId(), "ALL", 0, 10).size(), 2);
        assertEquals(bookingService.getForOwner(userDto1.getId(), "PAST", 0, 10).size(), 0);
        assertEquals(bookingService.getForOwner(userDto1.getId(), "FUTURE", 0, 10).size(), 1);
        assertEquals(bookingService.getForOwner(userDto1.getId(), "WAITING", 0, 10).size(), 1);
        assertEquals(bookingService.getForOwner(userDto1.getId(), "REJECTED", 0, 10).size(), 0);
    }
}
