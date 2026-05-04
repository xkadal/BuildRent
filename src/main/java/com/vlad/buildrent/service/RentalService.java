package com.vlad.buildrent.service;

import com.vlad.buildrent.model.*;
import com.vlad.buildrent.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final EquipmentService equipmentService;
    private final UserService userService;

    public List<Rental> findAll() {
        return rentalRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Rental> findByClient(User client) {
        return rentalRepository.findByClientOrderByCreatedAtDesc(client);
    }

    public Rental findById(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Замовлення не знайдено"));
    }

    @Transactional
    public Rental createRental(Long equipmentId, String clientEmail,
                                java.time.LocalDate startDate, java.time.LocalDate endDate) {
        User client = userService.getByEmail(clientEmail);
        Equipment equipment = equipmentService.findById(equipmentId);

        if (!equipment.isAvailable()) {
            throw new IllegalStateException("Обладнання недоступне для прокату");
        }

        Rental rental = Rental.builder()
                .client(client)
                .equipment(equipment)
                .startDate(startDate)
                .endDate(endDate)
                .status(RentalStatus.PENDING)
                .build();

        rental.calculateTotalPrice();
        return rentalRepository.save(rental);
    }

    @Transactional
    public Rental updateStatus(Long rentalId, RentalStatus status) {
        Rental rental = findById(rentalId);
        rental.setStatus(status);
        return rentalRepository.save(rental);
    }
}
