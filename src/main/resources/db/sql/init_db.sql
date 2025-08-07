CREATE TABLE products (
    id BIGSERIAL  NOT NULL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL UNIQUE,
    description VARCHAR(2000) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_date TIMESTAMP,
    created_user_id BIGINT NOT NULL,
    updated_user_id BIGINT
);

CREATE TABLE subcategories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_date TIMESTAMP,
    created_user_id BIGINT NOT NULL,
    updated_user_id BIGINT,
    CONSTRAINT fk_subcategory_category FOREIGN KEY (category_id) REFERENCES categories(id)
);