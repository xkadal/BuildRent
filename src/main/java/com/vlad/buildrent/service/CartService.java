package com.vlad.buildrent.service;

import com.vlad.buildrent.domain.Equipment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class CartService implements Serializable {

    private static final BigDecimal DELIVERY_FEE = new BigDecimal("200");

    private final EquipmentService equipmentService;
    private final AvailabilityService availabilityService;

    private final List<CartItem> items = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    public int totalQuantity() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public BigDecimal subtotal() {
        return items.stream()
                .map(CartItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal deliveryFee(boolean delivery) {
        return delivery ? DELIVERY_FEE : BigDecimal.ZERO;
    }

    public BigDecimal grandTotal(boolean delivery) {
        return subtotal().add(deliveryFee(delivery));
    }

    public CartItem add(Long equipmentId, int quantity, LocalDate start, LocalDate end) {
        validateRequest(quantity, start, end);
        Equipment eq = equipmentService.getById(equipmentId);
        int reservedInCart = items.stream()
                .filter(i -> i.getEquipmentId().equals(equipmentId)
                        && datesOverlap(i.getStartDate(), i.getEndDate(), start, end))
                .mapToInt(CartItem::getQuantity).sum();
        int available = availabilityService.availableQuantity(equipmentId, start, end);
        if (reservedInCart + quantity > available) {
            throw new IllegalStateException("Недостатньо вільних одиниць на обрані дати");
        }

        int days = AvailabilityService.daysBetween(start, end);
        CartItem item = new CartItem(
                sequence.getAndIncrement(),
                eq.getId(),
                eq.getSlug(),
                eq.getName(),
                eq.mainImage() != null ? eq.mainImage().getUrl() : null,
                eq.getPricePerDay(),
                quantity,
                start,
                end,
                days
        );
        items.add(item);
        return item;
    }

    public void updateQuantity(long lineId, int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Кількість повинна бути ≥ 1");
        CartItem item = findOrThrow(lineId);
        int available = availabilityService.availableQuantity(
                item.getEquipmentId(), item.getStartDate(), item.getEndDate());
        if (quantity > available) {
            throw new IllegalStateException("Доступно лише " + available + " одиниць на ці дати");
        }
        item.setQuantity(quantity);
    }

    public void remove(long lineId) {
        items.removeIf(i -> i.getId() == lineId);
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    private CartItem findOrThrow(long lineId) {
        return items.stream().filter(i -> i.getId() == lineId).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Позицію не знайдено"));
    }

    private void validateRequest(int quantity, LocalDate start, LocalDate end) {
        if (quantity < 1) throw new IllegalArgumentException("Кількість повинна бути ≥ 1");
        if (start == null || end == null) throw new IllegalArgumentException("Вкажіть дати оренди");
        if (end.isBefore(start)) throw new IllegalArgumentException("Дата завершення раніше за початок");
        if (start.isBefore(LocalDate.now())) throw new IllegalArgumentException("Дата початку у минулому");
    }

    private boolean datesOverlap(LocalDate s1, LocalDate e1, LocalDate s2, LocalDate e2) {
        return !s1.isAfter(e2) && !s2.isAfter(e1);
    }

    @Getter
    public static class CartItem implements Serializable {
        private final long id;
        private final Long equipmentId;
        private final String slug;
        private final String name;
        private final String imageUrl;
        private final BigDecimal pricePerDay;
        private int quantity;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final int days;

        public CartItem(long id, Long equipmentId, String slug, String name, String imageUrl,
                        BigDecimal pricePerDay, int quantity, LocalDate startDate, LocalDate endDate, int days) {
            this.id = id;
            this.equipmentId = equipmentId;
            this.slug = slug;
            this.name = name;
            this.imageUrl = imageUrl;
            this.pricePerDay = pricePerDay;
            this.quantity = quantity;
            this.startDate = startDate;
            this.endDate = endDate;
            this.days = days;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public BigDecimal lineTotal() {
            return pricePerDay.multiply(BigDecimal.valueOf((long) quantity * days));
        }
    }
}
