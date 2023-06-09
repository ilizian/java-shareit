package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long generateId = 0;

    @Override
    public Item getItem(long itemId) throws NotFoundException {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        }
        throw new NotFoundException("Ошибка. Невозможно получить вещь с id " + itemId);
    }

    @Override
    public Item addItem(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getItems(long userId) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : items.values()) {
            if (Objects.equals(item.getOwner().getId(), userId)) {
                itemList.add(item);
            }
        }
        return itemList;
    }

    @Override
    public Item updateItem(Item item, long itemId) throws NotFoundException {
        if (items.containsKey(item.getId())) {
            items.put(item.getId(), item);
            return item;
        }
        throw new NotFoundException("Ошибка. Неправильный id вещи");
    }

    @Override
    public void deleteItem(long itemId) {
        items.remove(itemId);
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> result = new ArrayList<>();
        if (text.isEmpty()) {
            return result;
        }
        String searchText = text.toLowerCase();
        for (Item item : items.values()) {
            String name = item.getName().toLowerCase();
            String description = item.getDescription().toLowerCase();
            if ((name.contains(searchText) || description.contains(searchText)) && item.getAvailable()) {
                result.add(item);
            }
        }
        return result;
    }

    private long generateId() {
        return ++generateId;
    }
}
