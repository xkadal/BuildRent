package com.vlad.buildrent.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationForm {

    @NotBlank(message = "Введіть ім'я")
    @Size(max = 80)
    private String firstName;

    @NotBlank(message = "Введіть прізвище")
    @Size(max = 80)
    private String lastName;

    @NotBlank(message = "Введіть email")
    @Email(message = "Невірний формат email")
    @Size(max = 190)
    private String email;

    @NotBlank(message = "Введіть телефон")
    @Pattern(regexp = "^\\+?[0-9 ()\\-]{7,32}$", message = "Невірний формат телефону")
    private String phone;

    @NotBlank(message = "Введіть пароль")
    @Size(min = 8, max = 100, message = "Пароль має бути від 8 до 100 символів")
    private String password;

    @NotBlank(message = "Підтвердіть пароль")
    private String confirmPassword;
}
