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

    public static ItemRequest toItemRequest(ItemRequestDtoResponse toItemRequestDtoResponse) {
        return new ItemRequest(null,
                toItemRequestDtoResponse.getDescription(),
                null,
                LocalDateTime.now());
    }
}
