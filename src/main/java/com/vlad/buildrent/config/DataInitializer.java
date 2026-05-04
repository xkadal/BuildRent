package com.vlad.buildrent.config;

import com.vlad.buildrent.model.*;
import com.vlad.buildrent.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EquipmentRepository equipmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Дані вже ініціалізовані, пропускаємо...");
            return;
        }

        log.info("Ініціалізація тестових даних...");

        // Створення адміністратора
        User admin = User.builder()
                .firstName("Адмін")
                .lastName("Системи")
                .email("admin@buildrent.pp.ua")
                .phone("+380991234567")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ROLE_ADMIN)
                .build();
        userRepository.save(admin);

        // Створення тестового клієнта
        User client = User.builder()
                .firstName("Іван")
                .lastName("Петренко")
                .email("client@buildrent.pp.ua")
                .phone("+380997654321")
                .password(passwordEncoder.encode("client123"))
                .role(Role.ROLE_CLIENT)
                .build();
        userRepository.save(client);

        // Категорії
        Category powerTools = categoryRepository.save(
                Category.builder().name("Електроінструменти").description("Дрилі, перфоратори, болгарки та інші електроінструменти").build()
        );
        Category heavyEquipment = categoryRepository.save(
                Category.builder().name("Важка техніка").description("Бетонозмішувачі, генератори, компресори").build()
        );
        Category handTools = categoryRepository.save(
                Category.builder().name("Ручний інструмент").description("Набори інструментів, драбини, будівельні ліса").build()
        );
        Category measuring = categoryRepository.save(
                Category.builder().name("Вимірювальне обладнання").description("Лазерні рівні, далекоміри, тепловізори").build()
        );

        // Обладнання
        List<Equipment> equipmentList = List.of(
                Equipment.builder().name("Перфоратор Bosch GBH 2-26").description("Потужний перфоратор для свердління бетону, цегли та каменю. Потужність 800 Вт.").pricePerDay(new BigDecimal("350")).quantity(5).category(powerTools).build(),
                Equipment.builder().name("Болгарка Makita GA5030").description("Кутова шліфмашина 125 мм, 720 Вт. Для різання та шліфування.").pricePerDay(new BigDecimal("250")).quantity(8).category(powerTools).build(),
                Equipment.builder().name("Зварювальний апарат Патон ВДІ-200S").description("Інверторний зварювальний апарат. Струм 200А.").pricePerDay(new BigDecimal("500")).quantity(3).category(powerTools).build(),
                Equipment.builder().name("Бетонозмішувач 180 л").description("Бетонозмішувач об'ємом 180 літрів. Потужність двигуна 800 Вт.").pricePerDay(new BigDecimal("600")).quantity(4).category(heavyEquipment).build(),
                Equipment.builder().name("Генератор бензиновий 5 кВт").description("Бензиновий генератор потужністю 5 кВт. Для автономного живлення.").pricePerDay(new BigDecimal("800")).quantity(3).category(heavyEquipment).build(),
                Equipment.builder().name("Компресор повітряний 50 л").description("Повітряний компресор з ресивером 50 л. Тиск до 8 бар.").pricePerDay(new BigDecimal("450")).quantity(4).category(heavyEquipment).build(),
                Equipment.builder().name("Драбина алюмінієва 3-секційна 3×12").description("Універсальна алюмінієва драбина. Максимальна висота 8.5 м.").pricePerDay(new BigDecimal("200")).quantity(6).category(handTools).build(),
                Equipment.builder().name("Будівельні ліса 2×6 м").description("Рамні будівельні ліса. Комплект на секцію 2×6 м.").pricePerDay(new BigDecimal("350")).quantity(10).category(handTools).build(),
                Equipment.builder().name("Лазерний рівень Bosch GLL 3-80").description("Професійний лазерний рівень з 3 площинами. Дальність 30 м.").pricePerDay(new BigDecimal("400")).quantity(3).category(measuring).build(),
                Equipment.builder().name("Тепловізор FLIR C5").description("Компактний тепловізор для обстеження будівель. Роздільна здатність 160×120.").pricePerDay(new BigDecimal("900")).quantity(2).category(measuring).build()
        );
        equipmentRepository.saveAll(equipmentList);

        log.info("Тестові дані успішно створено!");
    }
}
