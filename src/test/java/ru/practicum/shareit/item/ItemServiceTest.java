package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class ItemServiceTest {
    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void searchTest() throws ValidationException, NotFoundException {
        UserDto userDto = new UserDto(1L, "user", "user@test.ru");
        userService.addUser(userDto);
        ItemDto itemDto = new ItemDto(1L, "itemtest", "itemDescTest",
                true, null, null, null, null);
        itemService.addItem(itemDto, userDto.getId());
        assertNotNull(itemService.getItem(1L, 1L));
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE available = TRUE AND " +
                "(LOWER(description) LIKE '%' || :name || '%' OR LOWER(name) LIKE '%' || :name || '%')", Item.class);
        Item item = query
                .setParameter("name", itemDto.getName())
                .getSingleResult();
        assertEquals(item.getId(), 1L);
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
    }
}
