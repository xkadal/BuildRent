package com.vlad.buildrent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeForm {

    @NotBlank(message = "Введіть поточний пароль")
    private String currentPassword;

    @NotBlank @Size(min = 8, max = 100, message = "Пароль має бути від 8 до 100 символів")
    private String newPassword;

    @NotBlank
    private String confirmPassword;
}
