package com.vlad.buildrent.service;

import com.vlad.buildrent.domain.*;
import com.vlad.buildrent.repository.EquipmentRepository;
import com.vlad.buildrent.repository.ReviewRepository;
import com.vlad.buildrent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    public List<Review> findRecent(int limit) {
        return reviewRepository.findRecentApproved(PageRequest.of(0, limit));
    }

    public List<Review> approvedFor(Long equipmentId) {
        return reviewRepository.findByEquipmentIdAndApprovedTrueOrderByCreatedAtDesc(equipmentId);
    }

    public RatingSummary summary(Long equipmentId) {
        Double avg = reviewRepository.averageRating(equipmentId);
        long count = reviewRepository.countApproved(equipmentId);
        return new RatingSummary(avg == null ? 0.0 : avg, count);
    }

    public Page<Review> pendingPage(int page, int size) {
        return reviewRepository.findByApprovedFalseOrderByCreatedAtDesc(
                PageRequest.of(page, size));
    }

    @Transactional
    public Review create(Long equipmentId, Long clientId, Long rentalId, int rating, String text) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Рейтинг 1-5");
        if (rentalId != null && reviewRepository.existsByRentalIdAndClientId(rentalId, clientId)) {
            throw new IllegalArgumentException("Ви вже залишили відгук для цього замовлення");
        }
        Equipment eq = equipmentRepository.findById(equipmentId).orElseThrow();
        User client = userRepository.findById(clientId).orElseThrow();
        Review review = Review.builder()
                .equipment(eq).client(client)
                .rating(rating).text(text == null ? "" : text.trim())
                .approved(false).build();
        if (rentalId != null) {
            Rental r = new Rental();
            r.setId(rentalId);
            review.setRental(r);
        }
        return reviewRepository.save(review);
    }

    @Transactional
    public void approve(Long reviewId) {
        Review r = reviewRepository.findById(reviewId).orElseThrow();
        r.setApproved(true);
        reviewRepository.save(r);
    }

    @Transactional
    public void delete(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public record RatingSummary(double average, long count) {
        public String formatted() { return String.format("%.1f", average); }
    }
}
