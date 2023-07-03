package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDtoResponse> getByUser(@NotNull @RequestHeader("X-Sharer-User-Id") long userId)
            throws NotFoundException {
        List<ItemRequestDtoResponse> itemRequestDtoResponseList = itemRequestService.getByUser(userId);
        log.info("Запрос запроса пользователя с id " + userId);
        return itemRequestDtoResponseList;
    }

    @PostMapping
    public ItemRequestDtoResponse addItemRequest(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestBody ItemRequestDto itemRequestDto)
            throws ValidationException, NotFoundException {
        ItemRequestDtoResponse itemRequestDtoRes = itemRequestService.addItemRequest(userId, itemRequestDto);
        log.info("Создание запроса пользователя с id " + userId);
        return itemRequestDtoRes;
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getAll(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(value = "from", required = false, defaultValue = "0")
                                               @Min(0) int from,
                                               @RequestParam(value = "size", required = false, defaultValue = "100")
                                               @Min(1) int size) throws NotFoundException {
        List<ItemRequestDtoResponse> itemRequestDtoResponseList = itemRequestService.getAll(userId, from, size);
        log.info("Запрос запросов пользователя с id " + userId);
        return itemRequestDtoResponseList;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse getItemRequestById(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long requestId) throws NotFoundException {
        ItemRequestDtoResponse itemRequestDtoResponse = itemRequestService.getItemRequestById(requestId, userId);
        log.info("Запрос запроса с id " + requestId);
        return itemRequestDtoResponse;
    }
}
