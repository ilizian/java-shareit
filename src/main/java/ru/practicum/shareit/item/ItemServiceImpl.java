package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.misc.Status;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentStorage;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;
    private final Sort sort = Sort.by(Sort.Direction.ASC, "id");
    private final ItemRequestStorage itemRequestStorage;

    @Override
    public ItemDto getItem(long itemId, long userId) throws NotFoundException {
        Item item = validateItem(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (item.getOwner().getId() == userId) {
            setBookingsForItem(itemDto);
        }
        itemDto.setComments(commentStorage.findCommentsByItemOrderByCreatedDesc(item).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        return itemDto;
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, long userId) throws NotFoundException {
        User user = validateUser(userId);
        Item item = ItemMapper.toItem(itemDto, user);
        item.setOwner(user);
        if (Objects.nonNull(itemDto.getRequestId())) {
            ItemRequest itemRequest = itemRequestStorage.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new NotFoundException("Ошибка. Невозможно получить запрос с id  " + itemDto.getRequestId()));
            item.setRequest(itemRequest);
        }
        return ItemMapper.toItemDto(itemStorage.save(item));
    }

    @Override
    public ItemDto updateItem(long itemId, ItemDto itemDto, long userId) throws NotFoundException {
        User user = validateUser(userId);
        Item item = validateItem(itemId);
        if (!Objects.equals(item.getOwner().getId(), user.getId())) {
            throw new NotFoundException("Ошибка. Владелец вещи другой пользователь");
        }
        if (Objects.nonNull(itemDto.getName())) {
            item.setName(itemDto.getName());
        }
        if (Objects.nonNull(itemDto.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }
        if (Objects.nonNull(itemDto.getAvailable())) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemStorage.save(item));
    }

    @Override
    public List<ItemDto> getItems(long userId, int from, int size) throws NotFoundException {
        Optional<User> optionalUser = userStorage.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Ошибка. Пользователь не найден с id " + userId);
        }
        return getItemByUser(userId, from, size);
    }

    @Override
    public List<ItemDto> searchItems(String text, int from, int size) throws NotFoundException {
        PageRequest pageRequest = PageRequest.of(from, size, sort);
        String query = text.toLowerCase();
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemStorage.search(query, pageRequest);
        if (items.isEmpty()) {
            throw new NotFoundException("Ошибка. Не найдена вещь по фразе " + query);
        }
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemByUser(Long userId, int from, int size) throws NotFoundException {
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        List<Item> items = itemStorage.findAllByOwnerId(userId, pageRequest);
        List<ItemDto> itemsDto = new ArrayList<>();
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Ошибка. Невозможно получить пользователя с id  " + userId));
        List<Booking> bookingsLast = bookingStorage.findByItemOwnerAndEndBeforeOrderByIdDesc(user, LocalDateTime.now());
        List<Booking> bookingsNext = bookingStorage.findByItemOwnerAndStartAfterOrderByIdDesc(user, LocalDateTime.now());
        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            try {
                itemDto.setNextBooking(BookingMapper.toBookingDto(
                        Objects.requireNonNull(bookingsNext.stream()
                                .filter(booking -> booking.getItem().equals(item)).findFirst().orElse(null))
                ));
            } catch (NullPointerException e) {
                itemDto.setNextBooking(null);
            }
            try {
                itemDto.setLastBooking(BookingMapper.toBookingDto(
                        Objects.requireNonNull(bookingsLast.stream()
                                .filter(booking -> booking.getItem().equals(item)).findFirst().orElse(null))
                ));
            } catch (NullPointerException e) {
                itemDto.setLastBooking(null);
            }
            itemsDto.add(itemDto);
        }
        return itemsDto;
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) throws ValidationException,
            NotFoundException {
        User user = validateUser(userId);
        Item item = validateItem(itemId);
        List<Booking> bookings = bookingStorage
                .findAllByBookerIdAndItemIdAndStatusNot(userId, itemId, Status.REJECTED);
        if (bookings.isEmpty()) {
            throw new ValidationException("Ошибка. У предмета отсутствуют бронирования");
        }
        boolean future = true;
        for (Booking booking : bookings) {
            if (booking.getEnd().isBefore(LocalDateTime.now())) {
                future = false;
                break;
            }
        }
        if (future) {
            throw new ValidationException("Ошибка. Комментарий не может быть оставлен к будущему бронированию");
        }
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentStorage.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    private Item validateItem(long itemId) throws NotFoundException {
        return itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Ошибка. Не найдена вещь с id " + itemId));
    }

    private User validateUser(long userId) throws NotFoundException {
        return userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка. Не найден пользователь с id " + userId));
    }

    private void setBookingsForItem(ItemDto itemDto) {
        List<Booking> bookings = bookingStorage.findBookingsByItemIdOrderByStart(itemDto.getId());
        if (!bookings.isEmpty()) {
            Optional<Booking> lastBooking = bookings.stream()
                    .filter(booking -> !booking.getStatus().equals(Status.REJECTED) &&
                            booking.getStart().isBefore(LocalDateTime.now()))
                    .reduce((first, second) -> second);
            lastBooking.ifPresent(booking -> itemDto.setLastBooking(BookingMapper.toBookingDto(booking)));
            Optional<Booking> nextBooking = bookings.stream()
                    .filter(booking -> !booking.getStatus().equals(Status.REJECTED) &&
                            booking.getStart().isAfter(LocalDateTime.now()))
                    .findFirst();
            nextBooking.ifPresent(booking -> itemDto.setNextBooking(BookingMapper.toBookingDto(booking)));
        }
    }
}
