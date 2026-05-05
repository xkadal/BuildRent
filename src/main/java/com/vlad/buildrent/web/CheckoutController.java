package com.vlad.buildrent.web;

import com.vlad.buildrent.domain.DeliveryType;
import com.vlad.buildrent.domain.Rental;
import com.vlad.buildrent.dto.CheckoutForm;
import com.vlad.buildrent.security.AppUserPrincipal;
import com.vlad.buildrent.service.CartService;
import com.vlad.buildrent.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private static final BigDecimal DELIVERY_FEE = new BigDecimal("200");

    private final CartService cartService;
    private final RentalService rentalService;

    @GetMapping
    public String form(@AuthenticationPrincipal AppUserPrincipal principal,
                       @ModelAttribute("form") CheckoutForm form,
                       Model model,
                       RedirectAttributes ra) {
        if (cartService.isEmpty()) {
            ra.addFlashAttribute("error", "Кошик порожній");
            return "redirect:/cart";
        }
        if (form.getFullName() == null) form.setFullName(principal.getUser().fullName());
        if (form.getPhone() == null) form.setPhone(principal.getUser().getPhone());
        populateModel(model);
        return "checkout/form";
    }

    @PostMapping
    public String submit(@AuthenticationPrincipal AppUserPrincipal principal,
                         @Valid @ModelAttribute("form") CheckoutForm form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {
        if (cartService.isEmpty()) {
            ra.addFlashAttribute("error", "Кошик порожній");
            return "redirect:/cart";
        }
        if (form.getDeliveryType() == DeliveryType.DELIVERY
                && (form.getDeliveryAddress() == null || form.getDeliveryAddress().isBlank())) {
            binding.rejectValue("deliveryAddress", "required", "Вкажіть адресу доставки");
        }
        if (binding.hasErrors()) {
            populateModel(model);
            return "checkout/form";
        }
        try {
            Rental rental = rentalService.createFromCart(principal.getUser(), form);
            return "redirect:/checkout/payment/" + rental.getOrderNumber();
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/payment/{orderNumber}")
    public String payment(@AuthenticationPrincipal AppUserPrincipal principal,
                          @PathVariable String orderNumber,
                          Model model) {
        Rental rental = rentalService.getByOrderNumber(orderNumber);
        ensureOwner(principal, rental);
        model.addAttribute("rental", rental);
        return "checkout/payment";
    }

    @PostMapping("/payment/{orderNumber}")
    public String pay(@AuthenticationPrincipal AppUserPrincipal principal,
                      @PathVariable String orderNumber,
                      RedirectAttributes ra) {
        Rental rental = rentalService.getByOrderNumber(orderNumber);
        ensureOwner(principal, rental);
        try {
            rentalService.simulatePayment(orderNumber);
            return "redirect:/checkout/success/" + orderNumber;
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/checkout/payment/" + orderNumber;
        }
    }

    @GetMapping("/success/{orderNumber}")
    public String success(@AuthenticationPrincipal AppUserPrincipal principal,
                          @PathVariable String orderNumber,
                          Model model) {
        Rental rental = rentalService.getByOrderNumber(orderNumber);
        ensureOwner(principal, rental);
        model.addAttribute("rental", rental);
        return "checkout/success";
    }

    private void ensureOwner(AppUserPrincipal principal, Rental rental) {
        if (!rental.getClient().getId().equals(principal.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Чуже замовлення");
        }
    }

    private void populateModel(Model model) {
        model.addAttribute("items", cartService.getItems());
        model.addAttribute("subtotal", cartService.subtotal());
        model.addAttribute("deliveryFee", DELIVERY_FEE);
    }
}
