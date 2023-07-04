package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Test
    void dtoToItemRequestTest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("testDesc")
                .created(LocalDateTime.now())
                .build();
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(itemRequestDto);
        assertNotNull(itemRequest);
        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
    }
}
