package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item getItem(long itemId) throws NotFoundException;

    Item addItem(Item item);

    Item updateItem(Item item, long itemId) throws NotFoundException;

    List<Item> searchItems(String text);

    List<Item> getAllItems();
}
