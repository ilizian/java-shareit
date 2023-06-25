package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.misc.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingStorage extends JpaRepository<Booking, Long> {

    @Query(value = "select b from Booking as b " +
            "where b.booker.id =?1 and b.item.id = ?2 and b.status <> ?3 order by b.start desc")
    List<Booking> findBookingsByBookerAndItemAndStatusNot(Long userId, Long itemId, Status status);

    @Query(value = "select b from Booking as b where b.item.id = ?1 order by b.start asc")
    List<Booking> findBookingsByItemAsc(Long itemId);

    List<Booking> findAllByItemOwner(User owner, Sort sort);

    List<Booking> findAllByItemOwnerAndStatusEquals(User owner, Status status, Sort sort);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime start,
                                                              LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerAndEndBefore(User owner, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerAndStartAfter(User owner, LocalDateTime start, Sort sort);

    List<Booking> findAllByBooker(User booker, Sort sort);

    List<Booking> findAllByBookerAndStatusEquals(User booker, Status status, Sort sort);

    List<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime start, Sort sort);

    List<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime start,
                                                           LocalDateTime end, Sort sort);

}
