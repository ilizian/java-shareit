package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.misc.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public BookingDtoResponse getBooking(long bookingId, long userId) throws NotFoundException {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование " + bookingId + " не существует"));
        if (userId != booking.getItem().getOwner().getId() && userId != booking.getBooker().getId()) {
            throw new NotFoundException("Пользователь " + userId + " не имеет доступа");
        }
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public BookingDtoResponse createBooking(long userId, BookingDto bookingDto)
            throws NotFoundException, ValidationException {
        Item item = itemStorage.findById(bookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Ошибка. Невозможно получить вещь с id  " + bookingDto.getItemId()));
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Ошибка. Невозможно получить пользователя с id  " + userId));
        if (!item.getAvailable()) {
            throw new ValidationException("Ошибка. Вещь недоступна id " + bookingDto.getItemId());
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Ошибка. Пользователь является хозяином вещи");
        }
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start == null || end == null) {
            throw new ValidationException("Ошибка. Дата не может быть пустой");
        }
        if (end.isBefore(start) || end.equals(start)) {
            throw new ValidationException("Ошибка. Дата окончания бронирования не может быть меньше даты начала");
        }
        Booking booking = BookingMapper.dtoToBooking(bookingDto, user, item);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        return BookingMapper.toBookingDtoResponse(bookingStorage.save(booking));
    }

    @Override
    public BookingDtoResponse updateBooking(long userId, long bookingId, boolean isApproved)
            throws NotFoundException, ValidationException {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Ошибка. Невозможно получить бронирование с id " + bookingId));
        if (userId != booking.getItem().getOwner().getId()) {
            throw new NotFoundException("Ошибка. Нет доступа для изменения статуса бронирования");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("Ошибка. Статус бронирования уже изменён");
        }
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDtoResponse(bookingStorage.save(booking));
    }

    @Override
    public List<BookingDtoResponse> getBookingsOfUser(long userId, String state, int from, int size)
            throws NotFoundException {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Ошибка. Невозможно получить пользователя с id  " + userId));
        List<Booking> bookings;
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        switch (state) {
            case "ALL":
                bookings = bookingStorage.findAllByBooker(user, pageRequest);
                break;
            case "CURRENT":
                bookings = bookingStorage.findAllByBookerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), pageRequest);
                break;
            case "PAST":
                bookings = bookingStorage.findAllByBookerAndEndBefore(user, LocalDateTime.now(), pageRequest);
                break;
            case "FUTURE":
                bookings = bookingStorage.findAllByBookerAndStartAfter(user, LocalDateTime.now(), pageRequest);
                break;
            case "WAITING":
                bookings = bookingStorage.findAllByBookerAndStatusEquals(user, Status.WAITING, pageRequest);
                break;
            case "REJECTED":
                bookings = bookingStorage.findAllByBookerAndStatusEquals(user, Status.REJECTED, pageRequest);
                break;
            default:
                throw new UnknownStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoResponse> getForOwner(long userId, String state, int from, int size)
            throws NotFoundException {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Ошибка. Невозможно получить пользователя с id  " + userId));
        List<Booking> bookings;
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        switch (state) {
            case "PAST":
                bookings = bookingStorage.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), pageRequest);
                break;
            case "FUTURE":
                bookings = bookingStorage.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), pageRequest);
                break;
            case "ALL":
                bookings = bookingStorage.findAllByItemOwner(user, pageRequest);
                break;
            case "CURRENT":
                bookings = bookingStorage.findAllByItemOwnerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), pageRequest);
                break;
            case "WAITING":
                bookings = bookingStorage.findAllByItemOwnerAndStatusEquals(user, Status.WAITING, pageRequest);
                break;
            case "REJECTED":
                bookings = bookingStorage.findAllByItemOwnerAndStatusEquals(user, Status.REJECTED, pageRequest);
                break;
            default:
                throw new UnknownStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

}
