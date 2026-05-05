package com.vlad.buildrent.service;

import com.vlad.buildrent.domain.Category;
import com.vlad.buildrent.repository.CategoryRepository;
import com.vlad.buildrent.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAllByOrderBySortOrderAscNameAsc();
    }

    public Category findBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Категорію не знайдено: " + slug));
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Категорію не знайдено: " + id));
    }

    @Transactional
    public Category save(Category form) {
        if (form.getSlug() == null || form.getSlug().isBlank()) {
            form.setSlug(SlugUtil.slugify(form.getName()));
        }
        return categoryRepository.save(form);
    }

    @Transactional
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
