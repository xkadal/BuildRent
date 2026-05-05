package com.vlad.buildrent.web;

import com.vlad.buildrent.dto.RegistrationForm;
import com.vlad.buildrent.exception.EmailAlreadyTakenException;
import com.vlad.buildrent.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("error", "Невірний email або пароль");
        if (logout != null) model.addAttribute("success", "Ви вийшли з акаунта");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegistrationForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String submitRegister(@Valid @ModelAttribute("form") RegistrationForm form,
                                 BindingResult bindingResult) {
        if (form.getPassword() != null && !form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.addError(new FieldError("form", "confirmPassword", "Паролі не збігаються"));
        }
        if (bindingResult.hasErrors()) return "auth/register";
        try {
            var user = userService.register(form);
            userService.autoLogin(user);
            return "redirect:/?registered";
        } catch (EmailAlreadyTakenException ex) {
            bindingResult.addError(new FieldError("form", "email", ex.getMessage()));
            return "auth/register";
        }
    }
}
