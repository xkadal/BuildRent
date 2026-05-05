package com.vlad.buildrent.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProfileForm {

    @NotBlank @Size(max = 80)
    private String firstName;

    @NotBlank @Size(max = 80)
    private String lastName;

    @NotBlank @Pattern(regexp = "^\\+?[0-9 ()\\-]{7,32}$", message = "Невірний формат телефону")
    private String phone;
}
