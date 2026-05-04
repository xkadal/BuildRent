package com.vlad.buildrent.controller;

import com.vlad.buildrent.service.CategoryService;
import com.vlad.buildrent.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final EquipmentService equipmentService;
    private final CategoryService categoryService;

    @GetMapping
    public String catalog(Model model, @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("equipment", equipmentService.search(search));
            model.addAttribute("searchQuery", search);
        } else {
            model.addAttribute("equipment", equipmentService.findAvailable());
        }
        model.addAttribute("categories", categoryService.findAll());
        return "catalog";
    }

    @GetMapping("/{id}")
    public String equipmentDetails(@PathVariable Long id, Model model) {
        model.addAttribute("equipment", equipmentService.findById(id));
        return "equipment-details";
    }
}
