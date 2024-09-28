CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(255),
    email VARCHAR(255),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255),
    description VARCHAR(255),
    available   BOOLEAN,
    owner       BIGINT,
    CONSTRAINT fk_owner_for_items FOREIGN KEY (owner) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_date_time TIMESTAMP,
    end_date_time   TIMESTAMP,
    item            BIGINT,
    booker          BIGINT,
    status          VARCHAR(20),
    CONSTRAINT fk_item_for_bookings FOREIGN KEY (item) REFERENCES items (id),
    CONSTRAINT fk_booker_for_bookings FOREIGN KEY (booker) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    text    VARCHAR(255),
    item_id BIGINT,
    user_id BIGINT,
    created TIMESTAMP,
    CONSTRAINT fk_item_fo_comments FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT fk_user_for_comments FOREIGN KEY (user_id) REFERENCES users(id)
);