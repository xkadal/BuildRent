ALTER TABLE categories
    ADD COLUMN IF NOT EXISTS icon_class VARCHAR(60) NULL AFTER description;
