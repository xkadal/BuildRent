package com.vlad.buildrent.web.admin;

import com.vlad.buildrent.domain.Category;
import com.vlad.buildrent.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        if (!model.containsAttribute("category")) model.addAttribute("category", new Category());
        return "admin/categories";
    }

    @PostMapping
    public String save(@ModelAttribute Category category, RedirectAttributes ra) {
        try {
            categoryService.save(category);
            ra.addFlashAttribute("success", "Категорію збережено");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            categoryService.delete(id);
            ra.addFlashAttribute("success", "Категорію видалено");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", "Не вдалося видалити (можливо, на ній обладнання)");
        }
        return "redirect:/admin/categories";
    }
}
