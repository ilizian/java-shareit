package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping("{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                               @PathVariable long itemId) throws NotFoundException {
        log.info("Запрос вещи по id " + itemId);
        return itemService.getItem(itemId, userId);
    }

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") long userId) throws NotFoundException {
        log.info("Добавление вещи по пользовательскому id " + userId);
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@PathVariable long itemId,
                              @RequestBody ItemDto itemDto,
                              @NotBlank @RequestHeader("X-Sharer-User-Id") long userId) throws NotFoundException {
        log.info("Обновление вещи по id " + itemId);
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId) throws NotFoundException {
        log.info("Запрос списка вещей пользователя с id " + userId);
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) throws NotFoundException {
        log.info("Поиск по фразе:  " + text);
        return itemService.searchItems(text);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId, @Valid @RequestBody CommentDto commentDto) throws
            ValidationException, NotFoundException {
        log.info("Добавление комментария по id " + itemId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}
