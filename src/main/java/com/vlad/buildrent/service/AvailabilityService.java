package com.vlad.buildrent.service;

import com.vlad.buildrent.domain.Equipment;
import com.vlad.buildrent.domain.RentalStatus;
import com.vlad.buildrent.repository.EquipmentRepository;
import com.vlad.buildrent.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private static final List<RentalStatus> BLOCKING_STATUSES =
            List.of(RentalStatus.CONFIRMED, RentalStatus.PAID, RentalStatus.ACTIVE);

    private final EquipmentRepository equipmentRepository;
    private final RentalRepository rentalRepository;

    public int availableQuantity(Long equipmentId, LocalDate start, LocalDate end) {
        if (start == null || end == null || end.isBefore(start)) return 0;
        Equipment eq = equipmentRepository.findById(equipmentId).orElseThrow();
        int reserved = rentalRepository.sumReservedQuantity(equipmentId, start, end, BLOCKING_STATUSES);
        return Math.max(0, eq.getQuantityTotal() - reserved);
    }

    public boolean isAvailable(Long equipmentId, int quantity, LocalDate start, LocalDate end) {
        return availableQuantity(equipmentId, start, end) >= quantity;
    }

    public static int daysBetween(LocalDate start, LocalDate end) {
        return Math.max(1, (int) java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1);
    }
}
