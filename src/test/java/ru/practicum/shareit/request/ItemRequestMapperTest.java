package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest(ItemRequestMapper.class)
public class ItemRequestMapperTest {

    @Test
    void toItemRequestDtoResponseTest() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("testDesc")
                .created(LocalDateTime.now())
                .requestor(new User(1L, "user", "user@test.ru"))
                .build();
        ItemRequestDtoResponse itemRequestDtoResponse = ItemRequestMapper.toItemRequestDtoResponse(itemRequest);
        assertNotNull(itemRequestDtoResponse);
        assertEquals(itemRequestDtoResponse.getId(), itemRequest.getId());
        assertEquals(itemRequestDtoResponse.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestDtoResponse.getCreated(), itemRequest.getCreated());
    }

    @Test
    void toItemRequestTest() {
        ItemRequestDtoResponse itemRequestDtoResponse = ItemRequestDtoResponse.builder()
                .id(1L)
                .description("testDesc")
                .created(LocalDateTime.now())
                .build();
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoResponse);
        assertNotNull(itemRequest);
        assertEquals(itemRequest.getId(), itemRequestDtoResponse.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDtoResponse.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDtoResponse.getCreated());
    }
}
