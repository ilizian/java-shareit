package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.misc.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
public class Booking {
    private Long id;
    @NotNull
    private LocalDateTime start;
    private LocalDateTime end;
    @NotNull
    private Item item;
    @NotNull
    private User booker;
    private Status status;
}
