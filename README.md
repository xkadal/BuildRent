# BuildRent — сервіс прокату будівельного обладнання

Курсова робота з дисципліни «Об'єктно-орієнтоване програмування».
Веб-застосунок для оренди будівельної техніки з ролями клієнт / менеджер / адмін,
кошиком, чекаутом, імітацією оплати, поштовими сповіщеннями та повноцінною
адмін-панеллю.

## Стек технологій

- **Java 21** + **Spring Boot 3.4**
- **Spring Data JPA** (Hibernate) + **MariaDB 11**
- **Flyway** для версійованих міграцій схеми
- **Spring Security 6** з BCrypt + form-login
- **Thymeleaf** + **Bootstrap 5.3** + **Bootstrap Icons**
- **Spring Mail** (Mailtrap-сумісний у prod, лог-імплементація в dev)
- **Gradle KTS**, **Lombok**

## Архітектура

```
com.vlad.buildrent
├── config        — Spring/Security/Mvc конфігурація + DataSeeder
├── domain        — JPA entity-класи + enum-и (Role, RentalStatus, ...)
├── repository    — Spring Data JPA + Specification API
├── service       — бізнес-логіка (Equipment, Cart, Rental, Email, Reporting, ...)
├── web           — MVC-контролери (catalog, cart, checkout, account, manager, admin)
├── web.api       — REST для AJAX (availability, cart)
├── web.error     — кастомні error pages (404/500/403)
├── dto           — форм-DTO + view-моделі
├── exception     — бізнес-винятки
├── security      — UserDetails адаптер
└── util          — допоміжні утиліти (SlugUtil)
```

## Доменна модель

`User`, `Category`, `Equipment` (+ `EquipmentImage`, `EquipmentSpec`),
`Rental` (+ `RentalItem`), `Review`. Стани оренди описує enum `RentalStatus`:

```
PENDING → CONFIRMED → PAID → ACTIVE → RETURNED
        ↘ CANCELLED ↙
```

Доступність обчислюється запитом на перетин дат із сумуванням заброньованих
одиниць у статусах CONFIRMED/PAID/ACTIVE.

## Запуск локально

### 1. Створити базу

```bash
mysql -u root -p <<SQL
CREATE DATABASE build_buildrent CHARACTER SET utf8mb4;
CREATE USER 'build_buildrent'@'localhost' IDENTIFIED BY 'changeme';
GRANT ALL ON build_buildrent.* TO 'build_buildrent'@'localhost';
SQL
```

### 2. Налаштувати (за потреби)

`application.yml` або змінні оточення:

| Змінна          | Дефолт                                              |
|-----------------|-----------------------------------------------------|
| `DB_URL`        | `jdbc:mariadb://localhost:3306/build_buildrent`     |
| `DB_USERNAME`   | `build_buildrent`                                   |
| `DB_PASSWORD`   | `changeme`                                          |
| `UPLOADS_DIR`   | `./uploads`                                         |

### 3. Запустити

```bash
./gradlew bootRun
```

Flyway виконає міграції `V1`–`V4`, після чого `DataSeeder` (тільки на пустій БД)
заповнить тестових користувачів, категорії, обладнання та відгуки.

Додаток слухає на **http://localhost:8090**.

## Тестові акаунти

| Роль     | Email                       | Пароль       |
|----------|-----------------------------|--------------|
| Адмін    | admin@buildrent.pp.ua       | admin123     |
| Менеджер | manager@buildrent.pp.ua     | manager123   |
| Клієнт   | client@buildrent.pp.ua      | client123    |
| Клієнт   | olena@example.com           | client123    |

## Сторінки

**Публічні:** `/`, `/catalog`, `/catalog/{slug}`, `/category/{slug}`,
`/about`, `/contacts`, `/terms`, `/how-it-works`, `/login`, `/register`.

**Клієнт:** `/cart`, `/checkout`, `/checkout/payment/{n}`, `/checkout/success/{n}`,
`/account/profile`, `/account/orders`, `/account/orders/{n}` (скасування + відгук).

**Менеджер:** `/manager/orders`, `/manager/orders/{n}` (переходи статусу),
`/manager/calendar` (Ганттова сітка на 3 тижні).

**Адмін:** `/admin` (дашборд), `/admin/equipment` (CRUD з upload фото),
`/admin/categories`, `/admin/users` (зміна ролі / блокування),
`/admin/reviews` (модерація).

**REST:** `GET /api/availability?equipmentId=&start=&end=`,
`POST /api/cart/items`, `PATCH /api/cart/items/{id}`, `DELETE /api/cart/items/{id}`.

## Email-сервіс

- **dev-профіль:** `LoggingEmailService` — кожен лист логуєш у консоль із готовим
  Thymeleaf-шаблоном. Зручно під час розробки.
- **prod-профіль:** `SmtpEmailService` — реальна відправка через
  `JavaMailSender` (Mailtrap-сумісний).

Шаблони — у `src/main/resources/templates/email/`.

## Деплой

Автоматичний через GitHub Actions при push у `main`. Секрети:
`VPS_HOST`, `VPS_USER`, `VPS_SSH_KEY`.
