package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest(ItemMapper.class)
public class ItemMapperTest {
    @Test
    void toItemTest() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .description("itemTestDesc")
                .available(true)
                .name("itemTest")
                .build();
        Item item = ItemMapper.toItem(itemDto, null);
        assertNotNull(item);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }
    @Test
    void toItemDtoTest() {
        Item item = Item.builder()
                .id(1L)
                .description("itemTestDesc")
                .available(true)
                .name("itemTest")
                .request(new ItemRequest(1L, "testDesc", new User(), LocalDateTime.now()))
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertNotNull(itemDto);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(itemDto.getRequestId(), item.getRequest().getId());
    }

    @Test
    void toItemDtoBookingTest() {
        Item item = Item.builder()
                .id(1L)
                .name("itemTest")
                .build();
        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoBooking(item);
        assertNotNull(itemDtoBooking);
        assertEquals(itemDtoBooking.getId(), item.getId());
        assertEquals(itemDtoBooking.getName(), item.getName());
    }
}
