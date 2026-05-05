package com.vlad.buildrent.service;

import com.vlad.buildrent.domain.RentalStatus;
import com.vlad.buildrent.repository.EquipmentRepository;
import com.vlad.buildrent.repository.RentalRepository;
import com.vlad.buildrent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportingService {

    private final RentalRepository rentalRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    public DashboardSummary summary() {
        Instant monthAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        BigDecimal revenue = rentalRepository.sumPaidRevenueSince(monthAgo);
        long active = rentalRepository.countByStatus(RentalStatus.ACTIVE);
        long pending = rentalRepository.countByStatus(RentalStatus.PENDING);
        long paid = rentalRepository.countByStatus(RentalStatus.PAID);
        long totalEquipment = equipmentRepository.count();
        long totalUsers = userRepository.count();

        Map<String, Long> statusBreakdown = new LinkedHashMap<>();
        for (RentalStatus s : RentalStatus.values()) {
            statusBreakdown.put(s.name(), rentalRepository.countByStatus(s));
        }

        List<TopEquipment> topEquipment = rentalRepository.findTopEquipmentByRevenue(PageRequest.of(0, 5))
                .stream().map(row -> new TopEquipment(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (BigDecimal) row[2]
                )).toList();

        Map<LocalDate, Long> ordersByDay = new LinkedHashMap<>();
        for (Object[] row : rentalRepository.countOrdersByDay(monthAgo)) {
            LocalDate day = ((java.sql.Date) row[0]).toLocalDate();
            ordersByDay.put(day, ((Number) row[1]).longValue());
        }

        return new DashboardSummary(
                revenue == null ? BigDecimal.ZERO : revenue,
                active, pending, paid, totalEquipment, totalUsers,
                statusBreakdown, topEquipment, ordersByDay
        );
    }

    public record DashboardSummary(
            BigDecimal monthlyRevenue,
            long activeRentals,
            long pendingRentals,
            long paidRentals,
            long totalEquipment,
            long totalUsers,
            Map<String, Long> statusBreakdown,
            List<TopEquipment> topEquipment,
            Map<LocalDate, Long> ordersByDay
    ) {}

    public record TopEquipment(Long id, String name, BigDecimal revenue) {}
}
