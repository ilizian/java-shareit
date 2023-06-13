package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item getItem(long itemId) throws NotFoundException;

    Item addItem(Item item);

    List<Item> getItems(long userId);

    Item updateItem(Item item, long itemId) throws NotFoundException;

    void deleteItem(long itemId);

    List<Item> searchItems(String text);
}
