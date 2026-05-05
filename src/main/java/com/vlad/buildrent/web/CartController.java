package com.vlad.buildrent.web;

import com.vlad.buildrent.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String view(Model model) {
        model.addAttribute("items", cartService.getItems());
        model.addAttribute("subtotal", cartService.subtotal());
        return "cart/cart";
    }

    @PostMapping("/items/{id}/update")
    public String updateQty(@PathVariable("id") long id,
                            @RequestParam int quantity,
                            RedirectAttributes ra) {
        try {
            cartService.updateQuantity(id, quantity);
            ra.addFlashAttribute("success", "Кількість оновлено");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/items/{id}/remove")
    public String remove(@PathVariable("id") long id, RedirectAttributes ra) {
        cartService.remove(id);
        ra.addFlashAttribute("success", "Позицію видалено з кошика");
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clear(RedirectAttributes ra) {
        cartService.clear();
        ra.addFlashAttribute("success", "Кошик очищено");
        return "redirect:/cart";
    }
}
