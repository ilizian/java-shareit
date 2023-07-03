package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;
import java.util.List;

public interface BookingService {
    BookingDtoResponse updateBooking(long userId, long bookingId, boolean isApproved) throws NotFoundException,
            ValidationException;

    BookingDtoResponse getBooking(long bookingId, long userId) throws NotFoundException;

    BookingDtoResponse createBooking(long userId, BookingDto bookingDto) throws NotFoundException, ValidationException;

    Collection<BookingDtoResponse> getBookingsOfUser(long userId, String state, int from, int size)
            throws NotFoundException, ValidationException;

    List<BookingDtoResponse> getForOwner(long userId, String state, int from, int size) throws NotFoundException;
}