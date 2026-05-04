package com.vlad.buildrent.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ім'я не може бути порожнім")
    @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Прізвище не може бути порожнім")
    @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String lastName;

    @NotBlank(message = "Email не може бути порожнім")
    @Email(message = "Невірний формат email")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Телефон не може бути порожнім")
    @Column(nullable = false)
    private String phone;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.ROLE_CLIENT;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Rental> rentals = new ArrayList<>();

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
