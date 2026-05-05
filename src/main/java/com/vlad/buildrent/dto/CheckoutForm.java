package com.vlad.buildrent.dto;

import com.vlad.buildrent.domain.DeliveryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CheckoutForm {

    @NotBlank(message = "Вкажіть ПІБ")
    @Size(max = 200)
    private String fullName;

    @NotBlank(message = "Вкажіть телефон")
    @Size(max = 32)
    private String phone;

    @NotNull(message = "Оберіть спосіб отримання")
    private DeliveryType deliveryType = DeliveryType.PICKUP;

    @Size(max = 300)
    private String deliveryAddress;

    @Size(max = 1000)
    private String clientNotes;
}
