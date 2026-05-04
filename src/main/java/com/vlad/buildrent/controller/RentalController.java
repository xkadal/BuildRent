package com.vlad.buildrent.controller;

import com.vlad.buildrent.service.RentalService;
import com.vlad.buildrent.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;
    private final UserService userService;

    @GetMapping
    public String myRentals(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        var user = userService.getByEmail(userDetails.getUsername());
        model.addAttribute("rentals", rentalService.findByClient(user));
        return "my-rentals";
    }

    @PostMapping("/create")
    public String createRental(@RequestParam Long equipmentId,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        try {
            rentalService.createRental(equipmentId, userDetails.getUsername(), startDate, endDate);
            redirectAttributes.addFlashAttribute("success", "Замовлення успішно створено!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rentals";
    }
}
