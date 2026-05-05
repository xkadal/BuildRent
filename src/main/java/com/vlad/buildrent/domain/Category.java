package com.vlad.buildrent.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories", indexes = @Index(name = "idx_categories_slug", columnList = "slug", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 140)
    private String slug;

    @Column(length = 500)
    private String description;

    @Column(name = "icon_class", length = 60)
    private String iconClass;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
}
