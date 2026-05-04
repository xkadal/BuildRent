package com.vlad.buildrent.controller;

import com.vlad.buildrent.model.Equipment;
import com.vlad.buildrent.model.RentalStatus;
import com.vlad.buildrent.service.CategoryService;
import com.vlad.buildrent.service.EquipmentService;
import com.vlad.buildrent.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EquipmentService equipmentService;
    private final CategoryService categoryService;
    private final RentalService rentalService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("rentals", rentalService.findAll());
        model.addAttribute("equipment", equipmentService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/dashboard";
    }

    @GetMapping("/equipment/add")
    public String showAddEquipmentForm(Model model) {
        model.addAttribute("equipment", new Equipment());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/equipment-form";
    }

    @PostMapping("/equipment/save")
    public String saveEquipment(@ModelAttribute Equipment equipment,
                                @RequestParam Long categoryId,
                                RedirectAttributes redirectAttributes) {
        equipment.setCategory(categoryService.findById(categoryId));
        equipmentService.save(equipment);
        redirectAttributes.addFlashAttribute("success", "Обладнання збережено!");
        return "redirect:/admin";
    }

    @PostMapping("/rentals/{id}/status")
    public String updateRentalStatus(@PathVariable Long id,
                                     @RequestParam RentalStatus status,
                                     RedirectAttributes redirectAttributes) {
        rentalService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Статус замовлення оновлено!");
        return "redirect:/admin";
    }
}
