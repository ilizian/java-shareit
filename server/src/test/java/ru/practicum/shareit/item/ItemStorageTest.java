package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemStorageTest {
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserStorage userStorage;

    @Test
    void searchTest() {
        User user = new User(1L, "user", "user@test.ru");
        userStorage.save(user);
        Item item = new Item(1L, "itemtest", "itemDescTest", true, user, null);
        itemStorage.save(item);
        Item item2 = new Item(2L, "itemtest2", "itemDescTest2", true, user, null);
        itemStorage.save(item2);
        Item item3 = new Item(2L, "test", "itemDescTest3", true, user, null);
        itemStorage.save(item3);
        List<Item> items = itemStorage.search("itemtest", PageRequest.of(0, 100));
        assertEquals(items.get(0).getName(), item.getName());
        assertEquals(items.size(), 1);
    }
}
