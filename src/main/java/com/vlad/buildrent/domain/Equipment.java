package com.vlad.buildrent.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipment", indexes = {
        @Index(name = "idx_equipment_slug", columnList = "slug", unique = true),
        @Index(name = "idx_equipment_category", columnList = "category_id"),
        @Index(name = "idx_equipment_active", columnList = "active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false, unique = true, length = 180)
    private String slug;

    @Column(name = "short_description", length = 280)
    private String shortDescription;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "price_per_day", nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerDay;

    @Column(precision = 12, scale = 2)
    private BigDecimal deposit;

    @Column(name = "quantity_total", nullable = false)
    private int quantityTotal;

    @Column(length = 80)
    private String brand;

    @Column(length = 80)
    private String model;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<EquipmentImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @Builder.Default
    private List<EquipmentSpec> specs = new ArrayList<>();

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public EquipmentImage mainImage() {
        return images.stream().filter(EquipmentImage::isMain).findFirst()
                .orElse(images.isEmpty() ? null : images.get(0));
    }
}
