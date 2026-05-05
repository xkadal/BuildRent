package com.vlad.buildrent.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "equipment_specs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentSpec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "spec_value", nullable = false, length = 200)
    private String value;
}
