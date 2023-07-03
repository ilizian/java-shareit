package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestStorage itemRequestStorage;

    private final UserStorage userStorage;

    private final ItemStorage itemStorage;
    private final Sort sort = Sort.by(Sort.Direction.ASC, "created");

    @Override
    public ItemRequestDtoResponse addItemRequest(long userId, ItemRequestDto itemRequestDto)
            throws NotFoundException, ValidationException {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Ошибка. Невозможно получить пользователя с id  " + userId));
        if (Objects.isNull(itemRequestDto.getDescription())) {
            throw new ValidationException("Ошибка. Описание запроса не может быть пустым");
        }
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDtoResponse(itemRequestStorage.save(itemRequest));
    }

    @Override
    public ItemRequestDtoResponse getItemRequestById(long requestId, long userId) throws NotFoundException {
        ItemRequest itemRequest = itemRequestStorage.findById(requestId).orElseThrow(() ->
                new NotFoundException("Ошибка. Невозможно получить запрос с id  " + requestId));
        userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Ошибка. Невозможно получить пользователя с id  " + userId));
        ItemRequestDtoResponse itemRequestDtoResponse = ItemRequestMapper.toItemRequestDtoResponse(itemRequest);
        return setItems(itemRequestDtoResponse);
    }

    @Override
    public List<ItemRequestDtoResponse> getByUser(long userId) throws NotFoundException {
        userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Ошибка. Невозможно получить пользователя с id  " + userId));
        List<ItemRequest> itemRequestList =
                itemRequestStorage.findAllByRequestorIdOrderByCreatedAsc(userId);
        return setItemsList(itemRequestList);
    }

    @Override
    public List<ItemRequestDtoResponse> getAll(long userId, int from, int size) throws NotFoundException {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Ошибка. Невозможно получить пользователя с id  " + userId));
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        List<ItemRequest> itemRequestList = itemRequestStorage
                .findAllByRequestorNotLikeOrderByCreatedAsc(user, pageRequest);
        return setItemsList(itemRequestList);
    }

    private ItemRequestDtoResponse setItems(ItemRequestDtoResponse itemRequestDtoResponse) {
        itemRequestDtoResponse.setItems(itemStorage.findAllByRequestId(itemRequestDtoResponse.getId())
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
        return itemRequestDtoResponse;
    }

    private List<ItemRequestDtoResponse> setItemsList(List<ItemRequest> itemRequestList) {
        List<Item> items = itemStorage.findItemsByRequestNotNull();
        List<ItemRequestDtoResponse> itemRequestDtoResponseList = new ArrayList<>();
        Map<Long, List<ItemDto>> itemListMap = new HashMap<>();
        List<ItemDto> itemList = new ArrayList<>();
        for (Item item : items) {
            if (!itemListMap.containsKey(item.getRequest().getId())) {
                itemListMap.put(item.getRequest().getId(), itemList);
            }
            ItemDto itemDto = ItemMapper.toItemDto(item);
            itemList = itemListMap.get(item.getRequest().getId());
            itemList.add(itemDto);
            itemListMap.put(item.getRequest().getId(), itemList);
        }
        for (ItemRequest itemRequest : itemRequestList) {
            ItemRequestDtoResponse itemRequestDtoResponse = ItemRequestMapper.toItemRequestDtoResponse(itemRequest);
            if (itemListMap.size() > 0) {
                if (itemListMap.containsKey(itemRequest.getId())) {
                    itemRequestDtoResponse.setItems(itemListMap.get(itemRequest.getId()));
                }
            }
            itemRequestDtoResponseList.add(itemRequestDtoResponse);
        }
        return itemRequestDtoResponseList;
    }
}
