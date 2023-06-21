package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto getItem(long itemId) throws NotFoundException {
        return ItemMapper.toItemDto(itemStorage.getItem(itemId));
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, long userId) throws NotFoundException {
        User user = userStorage.getUser(userId);
        Item item = ItemMapper.toItem(itemDto, user);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemStorage.addItem(item));
    }

    @Override
    public ItemDto updateItem(long itemId, ItemDto itemDto, long userId) throws NotFoundException {
        Item item = itemStorage.getItem(itemId);
        User user = userStorage.getUser(userId);
        if (!Objects.equals(item.getOwner().getId(), user.getId())) {
            throw new NotFoundException("Владелец вещи другой пользователь");
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
        return ItemMapper.toItemDto(itemStorage.updateItem(item, itemId));
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        List<ItemDto> itemsDtoList = new ArrayList<>();
        List<Item> items = itemStorage.getItems(userId);
        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            itemsDtoList.add(itemDto);
        }
        return itemsDtoList;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<ItemDto> itemsDtoList = new ArrayList<>();
        List<Item> items = itemStorage.searchItems(text);
        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            itemsDtoList.add(itemDto);
        }
        return itemsDtoList;
    }
}
