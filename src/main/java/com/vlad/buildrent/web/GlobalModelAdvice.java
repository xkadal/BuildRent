package com.vlad.buildrent.web;

import com.vlad.buildrent.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final CartService cartService;

    @ModelAttribute("cartCount")
    public int cartCount() {
        return cartService.totalQuantity();
    }
}
