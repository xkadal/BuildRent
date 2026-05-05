package com.vlad.buildrent.repository;

import com.vlad.buildrent.domain.Role;
import com.vlad.buildrent.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    Page<User> findAllByRole(Role role, Pageable pageable);
}
