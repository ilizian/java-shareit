package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    @NotNull(message = "Неправильное имя")
    private String name;
    @Email(message = "Неправильный email")
    private String email;
}
