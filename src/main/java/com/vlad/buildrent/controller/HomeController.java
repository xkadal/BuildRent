package com.vlad.buildrent.controller;

import com.vlad.buildrent.service.CategoryService;
import com.vlad.buildrent.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CategoryService categoryService;
    private final EquipmentService equipmentService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("featuredEquipment", equipmentService.findAvailable());
        return "index";
    }
}
