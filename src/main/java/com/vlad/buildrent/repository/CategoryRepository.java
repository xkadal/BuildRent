package com.vlad.buildrent.repository;

import com.vlad.buildrent.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderBySortOrderAscNameAsc();
    Optional<Category> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
