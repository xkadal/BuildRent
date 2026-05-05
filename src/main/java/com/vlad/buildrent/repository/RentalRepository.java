package com.vlad.buildrent.repository;

import com.vlad.buildrent.domain.Rental;
import com.vlad.buildrent.domain.RentalStatus;
import com.vlad.buildrent.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    Optional<Rental> findByOrderNumber(String orderNumber);

    Page<Rental> findByClientOrderByCreatedAtDesc(User client, Pageable pageable);

    Page<Rental> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Rental> findByStatusOrderByCreatedAtDesc(RentalStatus status, Pageable pageable);

    long countByStatus(RentalStatus status);

    @Query("""
        select coalesce(sum(ri.quantity), 0)
        from RentalItem ri
        where ri.equipment.id = :equipmentId
          and ri.rental.status in :statuses
          and ri.rental.startDate <= :end
          and ri.rental.endDate   >= :start
    """)
    int sumReservedQuantity(@Param("equipmentId") Long equipmentId,
                            @Param("start") LocalDate start,
                            @Param("end") LocalDate end,
                            @Param("statuses") List<RentalStatus> statuses);

    @Query("""
        select coalesce(sum(r.total), 0)
        from Rental r
        where r.paymentStatus = com.vlad.buildrent.domain.PaymentStatus.PAID
          and r.paidAt >= :since
    """)
    BigDecimal sumPaidRevenueSince(@Param("since") Instant since);

    @Query("""
        select r from Rental r
        where r.status in :statuses
        order by r.startDate asc
    """)
    List<Rental> findActiveRentalsForCalendar(@Param("statuses") List<RentalStatus> statuses);

    @Query("""
        select ri.equipment.id, ri.equipment.name, sum(ri.lineTotal)
        from RentalItem ri
        where ri.rental.paymentStatus = com.vlad.buildrent.domain.PaymentStatus.PAID
        group by ri.equipment.id, ri.equipment.name
        order by sum(ri.lineTotal) desc
    """)
    List<Object[]> findTopEquipmentByRevenue(Pageable pageable);

    @Query("""
        select date(r.createdAt), count(r.id)
        from Rental r
        where r.createdAt >= :since
        group by date(r.createdAt)
        order by date(r.createdAt) asc
    """)
    List<Object[]> countOrdersByDay(@Param("since") Instant since);
}
