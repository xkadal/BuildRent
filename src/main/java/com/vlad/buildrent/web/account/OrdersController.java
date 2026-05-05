package com.vlad.buildrent.web.account;

import com.vlad.buildrent.domain.Rental;
import com.vlad.buildrent.domain.RentalStatus;
import com.vlad.buildrent.repository.RentalRepository;
import com.vlad.buildrent.security.AppUserPrincipal;
import com.vlad.buildrent.service.RentalService;
import com.vlad.buildrent.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/account/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final RentalRepository rentalRepository;
    private final RentalService rentalService;
    private final ReviewService reviewService;

    @GetMapping
    public String list(@AuthenticationPrincipal AppUserPrincipal principal,
                       @RequestParam(required = false, defaultValue = "0") int page,
                       Model model) {
        Page<Rental> rentals = rentalRepository.findByClientOrderByCreatedAtDesc(
                principal.getUser(), PageRequest.of(Math.max(0, page), 10));
        model.addAttribute("page", rentals);
        return "account/orders";
    }

    @GetMapping("/{orderNumber}")
    public String details(@AuthenticationPrincipal AppUserPrincipal principal,
                          @PathVariable String orderNumber,
                          Model model) {
        Rental rental = rentalService.getByOrderNumber(orderNumber);
        ensureOwner(principal, rental);
        model.addAttribute("rental", rental);
        return "account/order-details";
    }

    @PostMapping("/{orderNumber}/cancel")
    public String cancel(@AuthenticationPrincipal AppUserPrincipal principal,
                         @PathVariable String orderNumber,
                         RedirectAttributes ra) {
        Rental rental = rentalService.getByOrderNumber(orderNumber);
        ensureOwner(principal, rental);
        try {
            rentalService.cancel(orderNumber);
            ra.addFlashAttribute("success", "Замовлення скасовано");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/account/orders/" + orderNumber;
    }

    @PostMapping("/{orderNumber}/review")
    public String review(@AuthenticationPrincipal AppUserPrincipal principal,
                         @PathVariable String orderNumber,
                         @RequestParam Long equipmentId,
                         @RequestParam int rating,
                         @RequestParam(required = false) String text,
                         RedirectAttributes ra) {
        Rental rental = rentalService.getByOrderNumber(orderNumber);
        ensureOwner(principal, rental);
        if (rental.getStatus() != RentalStatus.RETURNED) {
            ra.addFlashAttribute("error", "Відгук можна залишити тільки після повернення");
            return "redirect:/account/orders/" + orderNumber;
        }
        try {
            reviewService.create(equipmentId, principal.getId(), rental.getId(), rating, text);
            ra.addFlashAttribute("success", "Дякуємо! Відгук буде опубліковано після модерації");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/account/orders/" + orderNumber;
    }

    private void ensureOwner(AppUserPrincipal principal, Rental rental) {
        if (!rental.getClient().getId().equals(principal.getId())) {
            throw new AccessDeniedException("Чуже замовлення");
        }
    }
}
