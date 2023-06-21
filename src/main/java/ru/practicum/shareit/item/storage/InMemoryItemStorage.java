package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> userItems = new HashMap<>();
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
        long ownerId = item.getOwner().getId();
        if (userItems.containsKey(ownerId)) {
            userItems.get(ownerId).add(item);
        } else {
            userItems.put(ownerId, new ArrayList<>());
            userItems.get(ownerId).add(item);
        }
        items.put(item.getId(), item);
        return item;
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

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getItems(long userId) {
        return userItems.get(userId);
    }

    private long generateId() {
        return ++generateId;
    }
}
