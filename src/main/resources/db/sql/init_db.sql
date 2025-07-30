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