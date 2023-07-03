package ru.practicum.shareit.item;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Добавление вещи по пользовательскому id " + userId);
        return itemClient.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDto itemDto,
                                             @PathVariable Long itemId) {
        log.info("Обновление вещи по id " + itemId);
        return itemClient.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId) {
        log.info("Запрос вещи по id " + itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                @Positive @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Запрос списка вещей пользователя с id " + userId);
        return itemClient.getItemByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam String text,
                                              @Min(0) @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                              @Min(1) @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("Поиск по фразе:  " + text);
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Min(1L) @PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("Добавление комментария по id " + itemId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}