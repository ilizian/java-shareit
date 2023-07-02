package ru.practicum.shareit.request;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse addItemRequest(long userId, ItemRequestDto itemRequestDto) throws NotFoundException, ValidationException;

    ItemRequestDtoResponse getItemRequestById(long requestId, long userId) throws NotFoundException;

    List<ItemRequestDtoResponse> getByUser(long userId) throws NotFoundException;

    List<ItemRequestDtoResponse> getAll(long userId, int from, int size) throws NotFoundException;
}
