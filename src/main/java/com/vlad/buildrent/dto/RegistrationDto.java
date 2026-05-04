package com.vlad.buildrent.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationDto {

    @NotBlank(message = "Ім'я не може бути порожнім")
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank(message = "Прізвище не може бути порожнім")
    @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank(message = "Email не може бути порожнім")
    @Email(message = "Невірний формат email")
    private String email;

    @NotBlank(message = "Телефон не може бути порожнім")
    private String phone;

    @NotBlank(message = "Пароль не може бути порожнім")
    @Size(min = 6, message = "Пароль повинен містити мінімум 6 символів")
    private String password;

    @NotBlank(message = "Підтвердіть пароль")
    private String confirmPassword;
}
