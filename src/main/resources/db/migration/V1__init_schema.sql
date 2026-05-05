CREATE TABLE users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    email           VARCHAR(190) NOT NULL,
    password_hash   VARCHAR(100) NOT NULL,
    first_name      VARCHAR(80)  NOT NULL,
    last_name       VARCHAR(80)  NOT NULL,
    phone           VARCHAR(32),
    role            VARCHAR(20)  NOT NULL,
    enabled         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL,
    CONSTRAINT uk_users_email UNIQUE (email)
);
CREATE INDEX idx_users_email ON users (email);

CREATE TABLE categories (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(120) NOT NULL,
    slug         VARCHAR(140) NOT NULL,
    description  VARCHAR(500),
    icon_class   VARCHAR(60),
    sort_order   INT NOT NULL DEFAULT 0,
    CONSTRAINT uk_categories_slug UNIQUE (slug)
);
CREATE INDEX idx_categories_slug ON categories (slug);

CREATE TABLE equipment (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    name               VARCHAR(160) NOT NULL,
    slug               VARCHAR(180) NOT NULL,
    short_description  VARCHAR(280),
    description        TEXT,
    price_per_day      DECIMAL(12,2) NOT NULL,
    deposit            DECIMAL(12,2),
    quantity_total     INT NOT NULL,
    brand              VARCHAR(80),
    model              VARCHAR(80),
    category_id        BIGINT NOT NULL,
    active             BOOLEAN NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMP NOT NULL,
    CONSTRAINT uk_equipment_slug UNIQUE (slug),
    CONSTRAINT fk_equipment_category FOREIGN KEY (category_id) REFERENCES categories (id)
);
CREATE INDEX idx_equipment_slug ON equipment (slug);
CREATE INDEX idx_equipment_category ON equipment (category_id);
CREATE INDEX idx_equipment_active ON equipment (active);

CREATE TABLE equipment_images (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    equipment_id  BIGINT NOT NULL,
    url           VARCHAR(500) NOT NULL,
    sort_order    INT NOT NULL DEFAULT 0,
    is_main       BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_image_equipment FOREIGN KEY (equipment_id) REFERENCES equipment (id) ON DELETE CASCADE
);

CREATE TABLE equipment_specs (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    equipment_id  BIGINT NOT NULL,
    name          VARCHAR(100) NOT NULL,
    spec_value    VARCHAR(200) NOT NULL,
    CONSTRAINT fk_spec_equipment FOREIGN KEY (equipment_id) REFERENCES equipment (id) ON DELETE CASCADE
);

CREATE TABLE rentals (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number      VARCHAR(32)  NOT NULL,
    client_id         BIGINT       NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    start_date        DATE         NOT NULL,
    end_date          DATE         NOT NULL,
    delivery_type     VARCHAR(16)  NOT NULL,
    delivery_address  VARCHAR(300),
    delivery_fee      DECIMAL(12,2) NOT NULL DEFAULT 0,
    subtotal          DECIMAL(12,2) NOT NULL,
    total             DECIMAL(12,2) NOT NULL,
    payment_status    VARCHAR(16)  NOT NULL,
    paid_at           TIMESTAMP NULL,
    client_notes      VARCHAR(1000),
    manager_notes     VARCHAR(1000),
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP NOT NULL,
    CONSTRAINT uk_rental_order_number UNIQUE (order_number),
    CONSTRAINT fk_rental_client FOREIGN KEY (client_id) REFERENCES users (id)
);
CREATE INDEX idx_rental_order_number ON rentals (order_number);
CREATE INDEX idx_rental_client_status ON rentals (client_id, status);
CREATE INDEX idx_rental_dates ON rentals (start_date, end_date);

CREATE TABLE rental_items (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    rental_id     BIGINT NOT NULL,
    equipment_id  BIGINT NOT NULL,
    quantity      INT NOT NULL,
    price_per_day DECIMAL(12,2) NOT NULL,
    days_count    INT NOT NULL,
    line_total    DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_item_rental    FOREIGN KEY (rental_id)    REFERENCES rentals (id) ON DELETE CASCADE,
    CONSTRAINT fk_item_equipment FOREIGN KEY (equipment_id) REFERENCES equipment (id)
);
CREATE INDEX idx_rental_item_rental ON rental_items (rental_id);
CREATE INDEX idx_rental_item_equipment ON rental_items (equipment_id);

CREATE TABLE reviews (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    equipment_id  BIGINT NOT NULL,
    client_id     BIGINT NOT NULL,
    rental_id     BIGINT,
    rating        INT NOT NULL,
    text          VARCHAR(2000),
    approved      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP NOT NULL,
    CONSTRAINT fk_review_equipment FOREIGN KEY (equipment_id) REFERENCES equipment (id) ON DELETE CASCADE,
    CONSTRAINT fk_review_client    FOREIGN KEY (client_id)    REFERENCES users (id),
    CONSTRAINT fk_review_rental    FOREIGN KEY (rental_id)    REFERENCES rentals (id) ON DELETE SET NULL
);
CREATE INDEX idx_review_equipment ON reviews (equipment_id);
CREATE INDEX idx_review_approved ON reviews (approved);
