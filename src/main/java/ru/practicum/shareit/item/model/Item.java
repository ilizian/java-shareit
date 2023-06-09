package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class Item {
    private Long id;
    @NotBlank(message = "Неправильное название")
    private String name;
    @NotBlank(message = "Неправильное описание")
    private String description;
    @NotNull(message = "Статус не определён")
    private Boolean available;
    @NotNull
    private User owner;
    private ItemRequest request;
}