package com.vlad.buildrent.repository;

import com.vlad.buildrent.domain.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long>, JpaSpecificationExecutor<Equipment> {

    Optional<Equipment> findBySlug(String slug);

    Page<Equipment> findAllByActiveTrue(Pageable pageable);

    @Query("select e from Equipment e where e.active = true order by e.createdAt desc")
    List<Equipment> findFeatured(Pageable pageable);

    List<Equipment> findAllByCategoryIdAndActiveTrue(Long categoryId);

    @Query("""
        select distinct e.brand from Equipment e
        where e.active = true and e.brand is not null and e.brand <> ''
        order by e.brand
    """)
    List<String> findDistinctBrands();
}
