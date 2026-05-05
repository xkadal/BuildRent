package com.vlad.buildrent.repository;

import com.vlad.buildrent.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByEquipmentIdAndApprovedTrueOrderByCreatedAtDesc(Long equipmentId);

    Page<Review> findByApprovedFalseOrderByCreatedAtDesc(Pageable pageable);

    boolean existsByRentalIdAndClientId(Long rentalId, Long clientId);

    @Query("""
        select avg(r.rating) from Review r
        where r.equipment.id = :equipmentId and r.approved = true
    """)
    Double averageRating(@Param("equipmentId") Long equipmentId);

    @Query("""
        select count(r) from Review r
        where r.equipment.id = :equipmentId and r.approved = true
    """)
    long countApproved(@Param("equipmentId") Long equipmentId);

    @Query("""
        select r from Review r
        where r.approved = true
        order by r.createdAt desc
    """)
    List<Review> findRecentApproved(Pageable pageable);
}
