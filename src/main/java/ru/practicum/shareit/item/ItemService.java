package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItem(long itemId, long userId) throws NotFoundException;

    ItemDto addItem(ItemDto itemDto, long userId) throws NotFoundException;

    ItemDto updateItem(long itemId, ItemDto itemDto, long userId) throws NotFoundException;

    List<ItemDto> getItems(long userId, int from, int size) throws NotFoundException;

    List<ItemDto> searchItems(String text, int from, int size) throws NotFoundException;

    List<ItemDto> getItemByUser(Long userId, int from, int size) throws NotFoundException;

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) throws ValidationException, NotFoundException;
}
