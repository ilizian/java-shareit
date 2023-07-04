package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.misc.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingStorage extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdAndItemIdAndStatusNot(Long userId, Long itemId, Status status);

    List<Booking> findAllByItemOwner(User owner, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStatusEquals(User owner, Status status, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime start,
                                                              LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerAndEndBefore(User owner, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartAfter(User owner, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBooker(User booker, Pageable pageable);

    List<Booking> findAllByBookerAndStatusEquals(User booker, Status status, Pageable pageable);

    List<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime start,
                                                           LocalDateTime end, Pageable pageable);

    List<Booking> findBookingsByItemIdOrderByStart(long itemId);

    List<Booking> findByItemOwnerAndEndBeforeOrderByIdDesc(User owner, LocalDateTime end);

    List<Booking> findByItemOwnerAndStartAfterOrderByIdDesc(User owner, LocalDateTime start);
}
