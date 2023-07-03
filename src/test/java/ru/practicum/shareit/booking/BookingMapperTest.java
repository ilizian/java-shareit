package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookingMapperTest {
    @Test
    void dtoToBookingTest() {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(1L)
                .bookerId(1L)
                .build();
        Booking booking = BookingMapper.dtoToBooking(bookingDto, null, null);
        assertNotNull(booking);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
    }

    @Test
    void toBookingDtoTest() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .item(new Item())
                .booker(new User())
                .build();
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        assertNotNull(bookingDto);
        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
    }

    @Test
    void toBookingDtoResponseTest() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .item(new Item())
                .booker(new User())
                .build();
        BookingDtoResponse bookingDtoResponse = BookingMapper.toBookingDtoResponse(booking);
        assertNotNull(bookingDtoResponse);
        assertEquals(bookingDtoResponse.getId(), booking.getId());
        assertEquals(bookingDtoResponse.getStart(), booking.getStart());
        assertEquals(bookingDtoResponse.getEnd(), booking.getEnd());
    }
}