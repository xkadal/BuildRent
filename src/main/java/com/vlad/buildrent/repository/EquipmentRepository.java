package com.vlad.buildrent.repository;

import com.vlad.buildrent.model.Category;
import com.vlad.buildrent.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByCategory(Category category);

    List<Equipment> findByAvailableTrue();

    List<Equipment> findByNameContainingIgnoreCase(String name);
}
