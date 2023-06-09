package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping("{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) throws NotFoundException {
        log.info("Запрос вещи по id " + itemId);
        return itemService.getItem(itemId);
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
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос списка вещей пользователя с id " + userId);
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Поиск по фразе:  " + text);
        return itemService.searchItems(text);
    }


}
