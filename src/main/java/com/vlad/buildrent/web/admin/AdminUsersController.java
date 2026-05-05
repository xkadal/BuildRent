package com.vlad.buildrent.web.admin;

import com.vlad.buildrent.domain.Role;
import com.vlad.buildrent.domain.User;
import com.vlad.buildrent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUsersController {

    private final UserRepository userRepository;

    @GetMapping
    public String list(@RequestParam(required = false) Role role,
                       @RequestParam(required = false, defaultValue = "0") int page,
                       Model model) {
        Page<User> users = (role == null)
                ? userRepository.findAll(PageRequest.of(Math.max(0, page), 20, Sort.by(Sort.Direction.DESC, "createdAt")))
                : userRepository.findAllByRole(role, PageRequest.of(Math.max(0, page), 20));
        model.addAttribute("page", users);
        model.addAttribute("activeRole", role);
        model.addAttribute("roles", Role.values());
        return "admin/users";
    }

    @PostMapping("/{id}/role")
    @Transactional
    public String changeRole(@PathVariable Long id, @RequestParam Role role, RedirectAttributes ra) {
        User user = userRepository.findById(id).orElseThrow();
        user.setRole(role);
        userRepository.save(user);
        ra.addFlashAttribute("success", "Роль оновлено для " + user.getEmail());
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/toggle-enabled")
    @Transactional
    public String toggleEnabled(@PathVariable Long id, RedirectAttributes ra) {
        User user = userRepository.findById(id).orElseThrow();
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        ra.addFlashAttribute("success",
                (user.isEnabled() ? "Розблоковано: " : "Заблоковано: ") + user.getEmail());
        return "redirect:/admin/users";
    }
}
