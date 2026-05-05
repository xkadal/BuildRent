package com.vlad.buildrent.service;

import com.vlad.buildrent.domain.Role;
import com.vlad.buildrent.domain.User;
import com.vlad.buildrent.dto.PasswordChangeForm;
import com.vlad.buildrent.dto.ProfileForm;
import com.vlad.buildrent.dto.RegistrationForm;
import com.vlad.buildrent.exception.EmailAlreadyTakenException;
import com.vlad.buildrent.repository.UserRepository;
import com.vlad.buildrent.security.AppUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegistrationForm form) {
        String email = form.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyTakenException(email);
        }
        User user = User.builder()
                .email(email)
                .firstName(form.getFirstName().trim())
                .lastName(form.getLastName().trim())
                .phone(form.getPhone().trim())
                .passwordHash(passwordEncoder.encode(form.getPassword()))
                .role(Role.ROLE_CLIENT)
                .enabled(true)
                .build();
        return userRepository.save(user);
    }

    public void autoLogin(User user) {
        AppUserPrincipal principal = new AppUserPrincipal(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal, null,
                List.of(new SimpleGrantedAuthority(user.getRole().name())));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Transactional
    public User updateProfile(Long userId, ProfileForm form) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setFirstName(form.getFirstName().trim());
        user.setLastName(form.getLastName().trim());
        user.setPhone(form.getPhone().trim());
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, PasswordChangeForm form) {
        User user = userRepository.findById(userId).orElseThrow();
        if (!passwordEncoder.matches(form.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Поточний пароль невірний");
        }
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new IllegalArgumentException("Паролі не збігаються");
        }
        user.setPasswordHash(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(user);
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }
}
