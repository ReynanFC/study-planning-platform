ALTER TABLE `user`
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL;

-- remove UNIQUE
ALTER TABLE category
    DROP INDEX category_name;

ALTER TABLE category
    ADD CONSTRAINT uk_category_name_user
        UNIQUE (category_name, user_id);