package com.vlad.buildrent.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "rentals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @NotNull(message = "Вкажіть дату початку")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Вкажіть дату закінчення")
    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RentalStatus status = RentalStatus.PENDING;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Розраховує загальну вартість прокату на основі кількості днів
     */
    public void calculateTotalPrice() {
        if (startDate != null && endDate != null && equipment != null) {
            long days = ChronoUnit.DAYS.between(startDate, endDate);
            if (days < 1) days = 1;
            this.totalPrice = equipment.getPricePerDay().multiply(BigDecimal.valueOf(days));
        }
    }
}
