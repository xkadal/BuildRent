package com.vlad.buildrent.controller;

import com.vlad.buildrent.dto.RegistrationDto;
import com.vlad.buildrent.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationDto", new RegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegistrationDto registrationDto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Паролі не співпадають");
        }

        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.register(registrationDto);
            redirectAttributes.addFlashAttribute("success", "Реєстрація успішна! Тепер ви можете увійти.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "error.email", e.getMessage());
            return "register";
        }
    }
}
