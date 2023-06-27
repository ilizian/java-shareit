package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static Booking dtoToBooking(BookingDto bookingDto, User user, Item item) {
        return new Booking(null,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                null);
    }

    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        return new BookingDtoResponse(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                UserMapper.toUserDtoBooking(booking.getBooker()),
                booking.getStatus(),
                ItemMapper.toItemDtoBooking(booking.getItem()));
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId());
    }

}