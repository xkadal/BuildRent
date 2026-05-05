package com.vlad.buildrent.config;

import com.vlad.buildrent.domain.*;
import com.vlad.buildrent.repository.*;
import com.vlad.buildrent.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EquipmentRepository equipmentRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("DataSeeder: users exist, skipping seed");
            return;
        }
        log.info("DataSeeder: starting initial seed");
        seedUsers();
        seedCategories();
        seedEquipment();
        seedReviews();
        log.info("DataSeeder: done");
    }

    private void seedUsers() {
        userRepository.save(User.builder()
                .email("admin@buildrent.pp.ua")
                .passwordHash(passwordEncoder.encode("admin123"))
                .firstName("Адміністратор").lastName("Системи")
                .phone("+380501234567").role(Role.ROLE_ADMIN).enabled(true).build());

        userRepository.save(User.builder()
                .email("manager@buildrent.pp.ua")
                .passwordHash(passwordEncoder.encode("manager123"))
                .firstName("Менеджер").lastName("Замовлень")
                .phone("+380502345678").role(Role.ROLE_MANAGER).enabled(true).build());

        userRepository.save(User.builder()
                .email("client@buildrent.pp.ua")
                .passwordHash(passwordEncoder.encode("client123"))
                .firstName("Іван").lastName("Будівельник")
                .phone("+380503456789").role(Role.ROLE_CLIENT).enabled(true).build());

        userRepository.save(User.builder()
                .email("olena@example.com")
                .passwordHash(passwordEncoder.encode("client123"))
                .firstName("Олена").lastName("Майстер")
                .phone("+380504567890").role(Role.ROLE_CLIENT).enabled(true).build());
    }

    private void seedCategories() {
        save(cat("Електроінструменти", "Дрилі, перфоратори, шуруповерти, шліфмашини", "bi-lightning-charge", 1));
        save(cat("Важка техніка", "Бетонозмішувачі, генератори, віброплити", "bi-truck", 2));
        save(cat("Ручний інструмент", "Молотки, рівні, інструменти для оздоблення", "bi-hammer", 3));
        save(cat("Вимірювальне обладнання", "Лазерні нівеліри, далекоміри, тепловізори", "bi-rulers", 4));
        save(cat("Підйомне обладнання", "Стрім'янки, вишки-тури, лебідки", "bi-arrow-up-square", 5));
        save(cat("Опалення та сушка", "Теплові гармати, сушильні панелі", "bi-thermometer-sun", 6));
    }

    private Category cat(String name, String desc, String icon, int order) {
        return Category.builder()
                .name(name).slug(SlugUtil.slugify(name))
                .description(desc).iconClass(icon).sortOrder(order).build();
    }

    private void save(Category c) { categoryRepository.save(c); }

    private void seedEquipment() {
        Category power = categoryRepository.findBySlug(SlugUtil.slugify("Електроінструменти")).orElseThrow();
        Category heavy = categoryRepository.findBySlug(SlugUtil.slugify("Важка техніка")).orElseThrow();
        Category hand = categoryRepository.findBySlug(SlugUtil.slugify("Ручний інструмент")).orElseThrow();
        Category measure = categoryRepository.findBySlug(SlugUtil.slugify("Вимірювальне обладнання")).orElseThrow();
        Category lift = categoryRepository.findBySlug(SlugUtil.slugify("Підйомне обладнання")).orElseThrow();
        Category heat = categoryRepository.findBySlug(SlugUtil.slugify("Опалення та сушка")).orElseThrow();

        // Електроінструменти
        equip(power, "Перфоратор Bosch GBH 2-26", "Bosch", "GBH 2-26",
                "Універсальний SDS-Plus перфоратор для свердління та довбання бетону.",
                "Надійний 800-Вт перфоратор для щоденних робіт. Енергія удару 2.7 Дж, три режими роботи, антивібраційна система.",
                "350.00", "1500.00", 5,
                List.of(spec("Потужність", "800 Вт"), spec("Енергія удару", "2.7 Дж"), spec("Патрон", "SDS-Plus"),
                        spec("Вага", "2.9 кг")),
                List.of("https://images.unsplash.com/photo-1572981779307-38b8cabb2407?w=800"));

        equip(power, "Шуруповерт DeWalt DCD777", "DeWalt", "DCD777",
                "Акумуляторний шуруповерт 18В з двома акумуляторами.",
                "Бездротова свобода: компактний, з безщітковим двигуном, 2 акумулятори 1.5 Ач у комплекті, кейс.",
                "180.00", "800.00", 8,
                List.of(spec("Напруга", "18 В"), spec("Макс. крутний момент", "55 Нм"),
                        spec("Швидкість", "0-1750 об/хв"), spec("Вага", "1.6 кг")),
                List.of("https://images.unsplash.com/photo-1530124566582-a618bc2615dc?w=800"));

        equip(power, "Болгарка Makita GA9020", "Makita", "GA9020",
                "Кутова шліфмашина 230 мм, 2200 Вт.",
                "Промислова болгарка для різання металу та каменю. Захист від випадкового пуску, бічна рукоятка.",
                "220.00", "1000.00", 4,
                List.of(spec("Потужність", "2200 Вт"), spec("Діаметр диска", "230 мм"),
                        spec("Швидкість", "6600 об/хв")),
                List.of("https://images.unsplash.com/photo-1581244277943-fe4a9c777189?w=800"));

        equip(power, "Лобзик Bosch GST 90 BE", "Bosch", "GST 90 BE",
                "Електролобзик з регулюванням обертів та маятниковим ходом.",
                "Точний рез у дереві, металі, пластику. Швидка зміна полотна, підсвітка робочої зони.",
                "150.00", "600.00", 3,
                List.of(spec("Потужність", "650 Вт"), spec("Глибина в дереві", "90 мм"),
                        spec("Глибина в металі", "10 мм")),
                List.of("https://images.unsplash.com/photo-1504148455328-c376907d081c?w=800"));

        // Важка техніка
        equip(heavy, "Бетонозмішувач Limex 165 LS", "Limex", "165 LS",
                "Бетонозмішувач 165 л для будівельних робіт.",
                "Об'єм бака 165 л, корисний об'єм 130 л. Потужний двигун, чавунний редуктор.",
                "450.00", "2500.00", 3,
                List.of(spec("Об'єм бака", "165 л"), spec("Корисний об'єм", "130 л"),
                        spec("Потужність", "850 Вт")),
                List.of("https://images.unsplash.com/photo-1581094289810-adf5d25690e3?w=800"));

        equip(heavy, "Генератор Honda EU22i", "Honda", "EU22i",
                "Інверторний генератор 2.2 кВт з низьким рівнем шуму.",
                "Чиста енергія для електроніки. Бак 3.6 л, до 8 годин роботи. Тихий режим Eco-Throttle.",
                "650.00", "5000.00", 2,
                List.of(spec("Потужність", "2.2 кВт"), spec("Об'єм бака", "3.6 л"),
                        spec("Шум", "57 дБ")),
                List.of("https://images.unsplash.com/photo-1581094488379-6b8e4e4d3f0a?w=800"));

        equip(heavy, "Віброплита Wacker Neuson WP1550", "Wacker Neuson", "WP1550",
                "Віброплита для ущільнення ґрунту та асфальту.",
                "Потужна одностороння віброплита для дорожніх та фундаментних робіт.",
                "520.00", "3000.00", 2,
                List.of(spec("Маса", "92 кг"), spec("Сила удару", "15 кН"),
                        spec("Двигун", "Honda GX160")),
                List.of("https://images.unsplash.com/photo-1504917595217-d4dc5ebe6122?w=800"));

        // Ручний інструмент
        equip(hand, "Набір ключів JONNESWAY 110 шт", "JONNESWAY", "S04H52110S",
                "Професійний набір ключів і головок у кейсі.",
                "Універсальний набір для автомобільних та будівельних робіт. Хром-ванадієва сталь.",
                "120.00", "800.00", 4,
                List.of(spec("К-сть інструментів", "110 шт"), spec("Матеріал", "Cr-V"),
                        spec("Кейс", "Так")),
                List.of("https://images.unsplash.com/photo-1530124566582-a618bc2615dc?w=800"));

        equip(hand, "Молоток слюсарний 800 г", "Stanley", "STHT0-51310",
                "Слюсарний молоток із склопластиковою рукояткою.",
                "Голівка з кованої сталі, термообробка. Антивібраційна рукоятка.",
                "40.00", "150.00", 10,
                List.of(spec("Маса голівки", "800 г"), spec("Довжина", "330 мм")),
                List.of("https://images.unsplash.com/photo-1581244277943-fe4a9c777189?w=800"));

        // Вимірювальне обладнання
        equip(measure, "Лазерний нівелір Bosch GLL 3-80", "Bosch", "GLL 3-80",
                "Тривимірний лазерний нівелір з трьома площинами 360°.",
                "3 лазерні площини 360° для вирівнювання по всій кімнаті. Дальність до 30 м.",
                "300.00", "2000.00", 3,
                List.of(spec("Кольорів площин", "3"), spec("Дальність", "30 м"),
                        spec("Точність", "± 0.2 мм/м")),
                List.of("https://images.unsplash.com/photo-1517089596392-fb9a9033e05b?w=800"));

        equip(measure, "Лазерний далекомір Leica DISTO D2", "Leica", "DISTO D2",
                "Компактний далекомір для замірів до 100 м.",
                "Bluetooth-підключення до телефону, точність ± 1.5 мм. Підходить для замірів у приміщеннях.",
                "180.00", "1200.00", 4,
                List.of(spec("Дальність", "100 м"), spec("Точність", "± 1.5 мм"),
                        spec("Bluetooth", "Так")),
                List.of("https://images.unsplash.com/photo-1581094288338-2314dddb7ece?w=800"));

        equip(measure, "Тепловізор FLIR ONE Pro", "FLIR", "ONE Pro",
                "Тепловізор для смартфонів — пошук тепловтрат та витоків.",
                "Діагностика тепловтрат у будинках, виявлення витоків води, перевірка електрики.",
                "400.00", "2500.00", 2,
                List.of(spec("Роздільна здатність", "160×120"), spec("Температурний діапазон", "-20…+400 °C"),
                        spec("Підключення", "USB-C / Lightning")),
                List.of("https://images.unsplash.com/photo-1518770660439-4636190af475?w=800"));

        // Підйомне обладнання
        equip(lift, "Драбина-трансформер 4×4", "Krause", "Multimatic 4x4",
                "Чотирисекційна драбина-трансформер до 4.7 м.",
                "Чотири положення: А-подібна, висувна, П-подібна, з помостом. Алюмінієвий каркас.",
                "100.00", "500.00", 5,
                List.of(spec("Секцій", "4×4"), spec("Макс. висота", "4.7 м"),
                        spec("Матеріал", "Алюміній")),
                List.of("https://images.unsplash.com/photo-1530124566582-a618bc2615dc?w=800"));

        equip(lift, "Вишка-тура Krause STABILO 5.4 м", "Krause", "STABILO Series 10",
                "Мобільна вишка-тура з робочою висотою 5.4 м.",
                "Швидке монтування, сертифіковано по EN 1004. Колеса з гальмами, поручні безпеки.",
                "550.00", "3500.00", 1,
                List.of(spec("Робоча висота", "5.4 м"), spec("Платформа", "0.6×1.8 м"),
                        spec("Сертифікація", "EN 1004")),
                List.of("https://images.unsplash.com/photo-1581094271901-8022df4466f9?w=800"));

        // Опалення
        equip(heat, "Теплова гармата Master B 5 EPR", "Master", "B 5 EPR",
                "Електрична теплова гармата 5 кВт.",
                "Швидке прогрівання приміщень до 50 м². Регулятор потужності 2.5 / 5 кВт, термостат.",
                "200.00", "800.00", 3,
                List.of(spec("Потужність", "5 кВт"), spec("Площа", "до 50 м²"),
                        spec("Живлення", "230 В")),
                List.of("https://images.unsplash.com/photo-1581092580497-e0d23cbdf1dc?w=800"));

        equip(heat, "Сушильна панель Trotec TTK 75 S", "Trotec", "TTK 75 S",
                "Осушувач повітря для будівельного просушування 30 л/добу.",
                "Швидке висушування стяжки, штукатурки. Гігростат, лічильник годин.",
                "350.00", "1500.00", 2,
                List.of(spec("Продуктивність", "30 л/добу"), spec("Площа", "до 80 м²"),
                        spec("Бак", "5.5 л")),
                List.of("https://images.unsplash.com/photo-1593696140826-c58b021acf8b?w=800"));
    }

    private EquipmentSpec spec(String n, String v) {
        return EquipmentSpec.builder().name(n).value(v).build();
    }

    private void equip(Category category, String name, String brand, String model,
                       String shortDesc, String desc, String pricePerDay, String deposit,
                       int qty, List<EquipmentSpec> specs, List<String> imageUrls) {
        Equipment e = Equipment.builder()
                .name(name).slug(SlugUtil.slugify(name + " " + brand + " " + model))
                .brand(brand).model(model)
                .shortDescription(shortDesc).description(desc)
                .pricePerDay(new BigDecimal(pricePerDay)).deposit(new BigDecimal(deposit))
                .quantityTotal(qty).category(category).active(true)
                .build();
        for (int i = 0; i < imageUrls.size(); i++) {
            EquipmentImage img = EquipmentImage.builder()
                    .equipment(e).url(imageUrls.get(i)).sortOrder(i).main(i == 0).build();
            e.getImages().add(img);
        }
        for (EquipmentSpec s : specs) {
            s.setEquipment(e);
            e.getSpecs().add(s);
        }
        equipmentRepository.save(e);
    }

    private void seedReviews() {
        var allEquipment = equipmentRepository.findAll();
        if (allEquipment.isEmpty()) return;
        var olena = userRepository.findByEmailIgnoreCase("olena@example.com").orElseThrow();
        var ivan = userRepository.findByEmailIgnoreCase("client@buildrent.pp.ua").orElseThrow();

        List.of(
                review(allEquipment.get(0), ivan, 5, "Чудовий перфоратор, тримає бетон легко. Орендував на тиждень — жодних проблем."),
                review(allEquipment.get(0), olena, 4, "Все ок, але важкуватий для тривалої роботи над головою."),
                review(allEquipment.get(1), ivan, 5, "Шуруповерт топ. Двох акумуляторів вистачає на цілий день."),
                review(allEquipment.get(4), olena, 5, "Бетонозмішувач справний, видали з заправленою оливою. Рекомендую!"),
                review(allEquipment.get(7), ivan, 4, "Набір якісний, кейс зручний. Бракувало пари головок дрібного розміру."),
                review(allEquipment.get(9), olena, 5, "Лазер точний, налаштовується за хвилину. Дуже допоміг при ремонті.")
        ).forEach(reviewRepository::save);
    }

    private Review review(Equipment eq, User client, int rating, String text) {
        return Review.builder()
                .equipment(eq).client(client)
                .rating(rating).text(text).approved(true).build();
    }
}
