package com.vlad.buildrent.web.admin;

import com.vlad.buildrent.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewsController {

    private final ReviewService reviewService;

    @GetMapping
    public String list(@RequestParam(required = false, defaultValue = "0") int page, Model model) {
        model.addAttribute("page", reviewService.pendingPage(page, 15));
        return "admin/reviews";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes ra) {
        reviewService.approve(id);
        ra.addFlashAttribute("success", "Відгук схвалено");
        return "redirect:/admin/reviews";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        reviewService.delete(id);
        ra.addFlashAttribute("success", "Відгук видалено");
        return "redirect:/admin/reviews";
    }
}
