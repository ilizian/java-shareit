package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import java.util.Collection;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("{bookingId}")
    public BookingDtoResponse getBooking(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") long userId)
            throws NotFoundException {
        log.info("Запрос бронирования " + bookingId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDtoResponse> getBookingsOfUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                            @RequestParam(defaultValue = "ALL") String state) throws
            NotFoundException, ValidationException {
        log.info("Запрос бронирования по пользователю " + userId);
        return bookingService.getBookingsOfUser(userId, state);
    }

    @PostMapping
    public BookingDtoResponse createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @Valid @RequestBody BookingDto bookingDto) throws ValidationException,
            NotFoundException {
        log.info("Создание бронирования " + userId);
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    public BookingDtoResponse updateBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam boolean approved, @PathVariable long bookingId) throws
            ValidationException, NotFoundException {
        log.info("Обновление бронирования " + bookingId);
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoResponse> getBookedItemOfOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                               @RequestParam(defaultValue = "ALL") String state) throws
            NotFoundException {
        log.info("Запрос бронированных предметов пользователя " + userId);
        return bookingService.getForOwner(userId, state);
    }
}