package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestStorage itemRequestStorage;

    private final UserStorage userStorage;

    private final ItemStorage itemStorage;
    private final Sort sort = Sort.by(Sort.Direction.ASC, "created");

    @Override
    public ItemRequestDtoResponse addItemRequest(long userId, ItemRequestDtoResponse itemRequestDtoResponse)
            throws NotFoundException, ValidationException {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Ошибка. Невозможно получить пользователя с id  " + userId));
        if (Objects.isNull(itemRequestDtoResponse.getDescription())) {
            throw new ValidationException("Ошибка. Описание запроса не может быть пустым");
        }
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoResponse);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestStorage.save(itemRequest);
        return ItemRequestMapper.toItemRequestDtoResponse(itemRequest);
    }

    @Override
    public ItemRequestDtoResponse getItemRequestById(long requestId, long userId) throws NotFoundException {
        ItemRequest itemRequest = itemRequestStorage.findById(requestId).orElseThrow(() ->
                new NotFoundException("Ошибка. Невозможно получить запрос с id  " + requestId));
        userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Ошибка. Невозможно получить пользователя с id  " + userId));
        ItemRequestDtoResponse itemRequestDtoResponse = ItemRequestMapper.toItemRequestDtoResponse(itemRequest);
        setItems(itemRequestDtoResponse);
        return itemRequestDtoResponse;
    }

    @Override
    public List<ItemRequestDtoResponse> getByUser(long userId) throws NotFoundException {
        userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Ошибка. Невозможно получить пользователя с id  " + userId));
        List<ItemRequest> itemRequestList =
                itemRequestStorage.findAllByRequestorIdOrderByCreatedAsc(userId);
        return itemRequestList
                .stream()
                .map(ItemRequestMapper::toItemRequestDtoResponse)
                .map(this::setItems)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoResponse> getAll(long userId, int from, int size) throws NotFoundException {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Ошибка. Невозможно получить пользователя с id  " + userId));
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        List<ItemRequest> itemRequestList = itemRequestStorage
                .findAllByRequestorNotLikeOrderByCreatedAsc(user, pageRequest);
        return itemRequestList
                .stream()
                .map(ItemRequestMapper::toItemRequestDtoResponse)
                .map(this::setItems)
                .collect(Collectors.toList());
    }

    private ItemRequestDtoResponse setItems(ItemRequestDtoResponse itemRequestDtoResponse) {
        itemRequestDtoResponse.setItems(itemStorage.findAllByRequestId(itemRequestDtoResponse.getId())
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
        return itemRequestDtoResponse;
    }
}
