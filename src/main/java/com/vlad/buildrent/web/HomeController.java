package com.vlad.buildrent.web;

import com.vlad.buildrent.service.CategoryService;
import com.vlad.buildrent.service.EquipmentService;
import com.vlad.buildrent.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CategoryService categoryService;
    private final EquipmentService equipmentService;
    private final ReviewService reviewService;

    @GetMapping("/")
    public String home(@RequestParam(required = false) String registered,
                       @RequestParam(required = false) String logout,
                       Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("featured", equipmentService.findFeatured(6));
        model.addAttribute("recentReviews", reviewService.findRecent(3));
        if (registered != null) model.addAttribute("success", "Реєстрація успішна. Ласкаво просимо!");
        if (logout != null) model.addAttribute("success", "Ви вийшли з акаунта");
        return "index";
    }

    @GetMapping("/about")
    public String about() { return "static/about"; }

    @GetMapping("/contacts")
    public String contacts() { return "static/contacts"; }

    @GetMapping("/terms")
    public String terms() { return "static/terms"; }

    @GetMapping("/how-it-works")
    public String howItWorks() { return "static/how-it-works"; }
}
