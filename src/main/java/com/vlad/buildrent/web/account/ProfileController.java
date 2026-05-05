package com.vlad.buildrent.web.account;

import com.vlad.buildrent.domain.User;
import com.vlad.buildrent.dto.PasswordChangeForm;
import com.vlad.buildrent.dto.ProfileForm;
import com.vlad.buildrent.security.AppUserPrincipal;
import com.vlad.buildrent.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/account/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public String view(@AuthenticationPrincipal AppUserPrincipal principal, Model model) {
        User user = userService.getById(principal.getId());
        ProfileForm pf = new ProfileForm();
        pf.setFirstName(user.getFirstName());
        pf.setLastName(user.getLastName());
        pf.setPhone(user.getPhone());
        model.addAttribute("user", user);
        if (!model.containsAttribute("profileForm")) model.addAttribute("profileForm", pf);
        if (!model.containsAttribute("passwordForm")) model.addAttribute("passwordForm", new PasswordChangeForm());
        return "account/profile";
    }

    @PostMapping
    public String save(@AuthenticationPrincipal AppUserPrincipal principal,
                       @Valid @ModelAttribute("profileForm") ProfileForm form,
                       BindingResult binding,
                       Model model,
                       RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("user", principal.getUser());
            model.addAttribute("passwordForm", new PasswordChangeForm());
            return "account/profile";
        }
        userService.updateProfile(principal.getId(), form);
        ra.addFlashAttribute("success", "Профіль оновлено");
        return "redirect:/account/profile";
    }

    @PostMapping("/password")
    public String changePassword(@AuthenticationPrincipal AppUserPrincipal principal,
                                 @Valid @ModelAttribute("passwordForm") PasswordChangeForm form,
                                 BindingResult binding,
                                 RedirectAttributes ra) {
        if (binding.hasErrors()) {
            ra.addFlashAttribute("error", "Перевірте поля паролю");
            return "redirect:/account/profile";
        }
        try {
            userService.changePassword(principal.getId(), form);
            ra.addFlashAttribute("success", "Пароль змінено");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/account/profile";
    }
}
