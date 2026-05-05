package com.vlad.buildrent.web.manager;

import com.vlad.buildrent.domain.Rental;
import com.vlad.buildrent.domain.RentalStatus;
import com.vlad.buildrent.repository.RentalRepository;
import com.vlad.buildrent.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/manager/orders")
@RequiredArgsConstructor
public class ManagerOrdersController {

    private final RentalRepository rentalRepository;
    private final RentalService rentalService;

    @GetMapping
    public String list(@RequestParam(required = false) RentalStatus status,
                       @RequestParam(required = false, defaultValue = "0") int page,
                       Model model) {
        Page<Rental> rentals = (status == null)
                ? rentalRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(Math.max(0, page), 15))
                : rentalRepository.findByStatusOrderByCreatedAtDesc(status, PageRequest.of(Math.max(0, page), 15));
        model.addAttribute("page", rentals);
        model.addAttribute("activeStatus", status);
        model.addAttribute("statuses", RentalStatus.values());
        return "manager/orders";
    }

    @GetMapping("/{orderNumber}")
    public String details(@PathVariable String orderNumber, Model model) {
        Rental rental = rentalService.getByOrderNumberWithItems(orderNumber);
        model.addAttribute("rental", rental);
        return "manager/order-details";
    }

    @PostMapping("/{orderNumber}/transition")
    public String transition(@PathVariable String orderNumber,
                             @RequestParam RentalStatus next,
                             RedirectAttributes ra) {
        try {
            rentalService.transition(orderNumber, next);
            ra.addFlashAttribute("success", "Статус оновлено");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/manager/orders/" + orderNumber;
    }
}
