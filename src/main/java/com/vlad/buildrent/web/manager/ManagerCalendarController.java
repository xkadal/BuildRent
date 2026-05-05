package com.vlad.buildrent.web.manager;

import com.vlad.buildrent.domain.Rental;
import com.vlad.buildrent.domain.RentalStatus;
import com.vlad.buildrent.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/manager/calendar")
@RequiredArgsConstructor
public class ManagerCalendarController {

    private final RentalRepository rentalRepository;

    @GetMapping
    public String calendar(Model model) {
        LocalDate today = LocalDate.now();
        LocalDate horizon = today.plusDays(21);
        List<Rental> rentals = rentalRepository.findActiveRentalsForCalendar(
                List.of(RentalStatus.CONFIRMED, RentalStatus.PAID, RentalStatus.ACTIVE));
        model.addAttribute("rentals", rentals);
        model.addAttribute("days", today.datesUntil(horizon.plusDays(1)).toList());
        model.addAttribute("today", today);
        return "manager/calendar";
    }
}
