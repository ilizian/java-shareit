package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ItemRequestMapper {
    public static ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest) {
        return new ItemRequestDtoResponse(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                new ArrayList<>());
    }

    public static ItemRequest toItemRequest(ItemRequestDtoResponse itemRequestDtoResponse) {
        return new ItemRequest(itemRequestDtoResponse.getId(),
                itemRequestDtoResponse.getDescription(),
                null,
                LocalDateTime.now());
    }
}
