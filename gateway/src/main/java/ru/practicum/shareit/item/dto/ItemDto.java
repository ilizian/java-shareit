package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Неправильное название")
    private String name;
    @NotBlank(message = "Неправильное описание")
    private String description;
    @NotNull(message = "Статус не определён")
    private Boolean available;
    private Long requestId;
}