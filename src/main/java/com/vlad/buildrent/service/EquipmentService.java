package com.vlad.buildrent.service;

import com.vlad.buildrent.domain.Category;
import com.vlad.buildrent.domain.Equipment;
import com.vlad.buildrent.repository.CategoryRepository;
import com.vlad.buildrent.repository.EquipmentRepository;
import com.vlad.buildrent.util.SlugUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Equipment> findFeatured(int limit) {
        List<Equipment> items = equipmentRepository.findFeatured(PageRequest.of(0, limit));
        items.forEach(this::initializeCardRelations);
        return items;
    }

    @Transactional(readOnly = true)
    public Equipment findBySlug(String slug) {
        Equipment equipment = equipmentRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Обладнання не знайдено: " + slug));
        initializeDetailsRelations(equipment);
        return equipment;
    }

    @Transactional(readOnly = true)
    public Equipment getById(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Обладнання не знайдено: " + id));
        initializeDetailsRelations(equipment);
        return equipment;
    }

    public List<String> findDistinctBrands() {
        return equipmentRepository.findDistinctBrands();
    }

    @Transactional(readOnly = true)
    public Page<Equipment> search(EquipmentFilter filter, int page, int size, String sortKey) {
        Sort sort = switch (sortKey == null ? "" : sortKey) {
            case "price-asc"  -> Sort.by("pricePerDay").ascending();
            case "price-desc" -> Sort.by("pricePerDay").descending();
            case "name"       -> Sort.by("name").ascending();
            default           -> Sort.by("createdAt").descending();
        };
        Pageable pageable = PageRequest.of(Math.max(0, page), size, sort);
        Page<Equipment> result = equipmentRepository.findAll(buildSpec(filter), pageable);
        result.getContent().forEach(this::initializeCardRelations);
        return result;
    }

    @Transactional(readOnly = true)
    public Page<Equipment> adminPage(int page, int size) {
        Page<Equipment> result = equipmentRepository.findAll(
                PageRequest.of(Math.max(0, page), size, Sort.by(Sort.Direction.DESC, "createdAt")));
        result.getContent().forEach(this::initializeCardRelations);
        return result;
    }

    private Specification<Equipment> buildSpec(EquipmentFilter f) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("active")));

            if (f.categorySlug() != null && !f.categorySlug().isBlank()) {
                predicates.add(cb.equal(root.get("category").get("slug"), f.categorySlug()));
            }
            if (f.query() != null && !f.query().isBlank()) {
                String like = "%" + f.query().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("brand")), like),
                        cb.like(cb.lower(root.get("shortDescription")), like)
                ));
            }
            if (f.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("pricePerDay"), f.minPrice()));
            }
            if (f.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("pricePerDay"), f.maxPrice()));
            }
            if (f.brand() != null && !f.brand().isBlank()) {
                predicates.add(cb.equal(root.get("brand"), f.brand()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Transactional
    public Equipment save(Equipment equipment) {
        if (equipment.getSlug() == null || equipment.getSlug().isBlank()) {
            equipment.setSlug(SlugUtil.slugify(
                    equipment.getName() + "-" + (equipment.getBrand() != null ? equipment.getBrand() : "")));
        }
        return equipmentRepository.save(equipment);
    }

    @Transactional
    public void deactivate(Long id) {
        Equipment e = getById(id);
        e.setActive(false);
        equipmentRepository.save(e);
    }

    @Transactional
    public void delete(Long id) {
        equipmentRepository.deleteById(id);
    }

    public List<Category> allCategories() {
        return categoryRepository.findAllByOrderBySortOrderAscNameAsc();
    }

    private void initializeCardRelations(Equipment equipment) {
        equipment.getCategory().getName();
        equipment.getImages().size();
    }

    private void initializeDetailsRelations(Equipment equipment) {
        initializeCardRelations(equipment);
        equipment.getSpecs().size();
    }

    public record EquipmentFilter(
            String query,
            String categorySlug,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String brand
    ) {}
}
