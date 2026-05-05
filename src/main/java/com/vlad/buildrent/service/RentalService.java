package com.vlad.buildrent.service;

import com.vlad.buildrent.domain.*;
import com.vlad.buildrent.dto.CheckoutForm;
import com.vlad.buildrent.exception.IllegalStateTransitionException;
import com.vlad.buildrent.exception.NotEnoughAvailabilityException;
import com.vlad.buildrent.repository.EquipmentRepository;
import com.vlad.buildrent.repository.RentalRepository;
import com.vlad.buildrent.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RentalService {

    private static final BigDecimal DELIVERY_FEE = new BigDecimal("200");
    private static final DateTimeFormatter ORDER_DATE_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.systemDefault());

    private final RentalRepository rentalRepository;
    private final EquipmentRepository equipmentRepository;
    private final CartService cartService;
    private final AvailabilityService availabilityService;
    private final EmailService emailService;

    @Transactional
    public Rental createFromCart(User client, CheckoutForm form) {
        List<CartService.CartItem> cartItems = cartService.getItems();
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Кошик порожній");
        }

        LocalDate earliestStart = cartItems.stream()
                .map(CartService.CartItem::getStartDate).min(LocalDate::compareTo).orElseThrow();
        LocalDate latestEnd = cartItems.stream()
                .map(CartService.CartItem::getEndDate).max(LocalDate::compareTo).orElseThrow();

        boolean isDelivery = form.getDeliveryType() == DeliveryType.DELIVERY;
        BigDecimal deliveryFee = isDelivery ? DELIVERY_FEE : BigDecimal.ZERO;
        BigDecimal subtotal = cartService.subtotal();
        BigDecimal total = subtotal.add(deliveryFee);

        Rental rental = Rental.builder()
                .orderNumber(generateOrderNumber())
                .client(client)
                .status(RentalStatus.PENDING)
                .startDate(earliestStart)
                .endDate(latestEnd)
                .deliveryType(form.getDeliveryType())
                .deliveryAddress(isDelivery ? form.getDeliveryAddress() : null)
                .deliveryFee(deliveryFee)
                .subtotal(subtotal)
                .total(total)
                .paymentStatus(PaymentStatus.UNPAID)
                .clientNotes(form.getClientNotes())
                .build();

        for (CartService.CartItem ci : cartItems) {
            int available = availabilityService.availableQuantity(
                    ci.getEquipmentId(), ci.getStartDate(), ci.getEndDate());
            if (available < ci.getQuantity()) {
                throw new NotEnoughAvailabilityException(ci.getName(), ci.getQuantity(), available);
            }
            Equipment eq = equipmentRepository.findById(ci.getEquipmentId()).orElseThrow();
            BigDecimal lineTotal = ci.lineTotal();
            RentalItem item = RentalItem.builder()
                    .equipment(eq)
                    .quantity(ci.getQuantity())
                    .pricePerDay(ci.getPricePerDay())
                    .daysCount(ci.getDays())
                    .lineTotal(lineTotal)
                    .build();
            rental.addItem(item);
        }

        Rental saved = rentalRepository.save(rental);
        cartService.clear();
        emailService.sendOrderCreated(saved);
        return saved;
    }

    @Transactional
    public Rental transition(String orderNumber, RentalStatus next) {
        Rental rental = getByOrderNumber(orderNumber);
        if (!rental.getStatus().canTransitionTo(next)) {
            throw new IllegalStateTransitionException(
                    "Неможливий перехід " + rental.getStatus() + " → " + next);
        }
        rental.setStatus(next);
        switch (next) {
            case CONFIRMED -> emailService.sendOrderConfirmed(rental);
            case RETURNED -> emailService.sendOrderReturned(rental);
            case CANCELLED -> emailService.sendOrderCancelled(rental);
            default -> { }
        }
        return rental;
    }

    @Transactional
    public Rental simulatePayment(String orderNumber) {
        Rental rental = getByOrderNumber(orderNumber);
        if (rental.getStatus() != RentalStatus.PENDING && rental.getStatus() != RentalStatus.CONFIRMED) {
            throw new IllegalStateTransitionException("Замовлення не можна оплатити в поточному статусі");
        }
        rental.setStatus(RentalStatus.PAID);
        rental.setPaymentStatus(PaymentStatus.PAID);
        rental.setPaidAt(Instant.now());
        emailService.sendOrderPaid(rental);
        return rental;
    }

    @Transactional
    public Rental cancel(String orderNumber) {
        Rental rental = getByOrderNumber(orderNumber);
        if (!rental.getStatus().canTransitionTo(RentalStatus.CANCELLED)) {
            throw new IllegalStateTransitionException("Замовлення не можна скасувати");
        }
        rental.setStatus(RentalStatus.CANCELLED);
        emailService.sendOrderCancelled(rental);
        return rental;
    }

    public Rental getByOrderNumber(String orderNumber) {
        return rentalRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Замовлення не знайдено: " + orderNumber));
    }

    @Transactional(readOnly = true)
    public Rental getByOrderNumberWithItems(String orderNumber) {
        return rentalRepository.findByOrderNumberWithItems(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Замовлення не знайдено: " + orderNumber));
    }

    private String generateOrderNumber() {
        String date = ORDER_DATE_FMT.format(Instant.now());
        int suffix = ThreadLocalRandom.current().nextInt(100, 1000);
        String candidate = "BR-" + date + "-" + suffix;
        while (rentalRepository.findByOrderNumber(candidate).isPresent()) {
            suffix = ThreadLocalRandom.current().nextInt(100, 1000);
            candidate = "BR-" + date + "-" + suffix;
        }
        return candidate;
    }
}
