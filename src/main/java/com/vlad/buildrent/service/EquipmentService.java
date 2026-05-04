package com.vlad.buildrent.service;

import com.vlad.buildrent.model.Equipment;
import com.vlad.buildrent.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public List<Equipment> findAll() {
        return equipmentRepository.findAll();
    }

    public List<Equipment> findAvailable() {
        return equipmentRepository.findByAvailableTrue();
    }

    public Equipment findById(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Обладнання не знайдено"));
    }

    public List<Equipment> search(String query) {
        return equipmentRepository.findByNameContainingIgnoreCase(query);
    }

    @Transactional
    public Equipment save(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    @Transactional
    public void deleteById(Long id) {
        equipmentRepository.deleteById(id);
    }
}
