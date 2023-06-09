package ru.practicum.shareit.item;

import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItem(long itemId) throws NotFoundException;

    ItemDto addItem(ItemDto itemDto, long userId) throws NotFoundException;

    ItemDto updateItem(long itemId, ItemDto itemDto, long userId) throws NotFoundException;

    List<ItemDto> getItems(long userId);

    List<ItemDto> searchItems(String text);

}
