CREATE TABLE IF NOT EXISTS users
(
    id    INT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(255),
    email VARCHAR(255),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    user_id     INT,
    created     TIMESTAMP,

    CONSTRAINT fk_request_to_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255),
    description VARCHAR(255),
    available   BOOLEAN,
    owner       INT,
    request_id  INT,

    CONSTRAINT fk_owner_for_items FOREIGN KEY (owner) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    start_date_time TIMESTAMP,
    end_date_time   TIMESTAMP,
    item            INT,
    booker          INT,
    status          VARCHAR(20),

    CONSTRAINT fk_item FOREIGN KEY (item) REFERENCES items (id),
    CONSTRAINT fk_booker FOREIGN KEY (booker) REFERENCES users (id)
);


CREATE TABLE IF NOT EXISTS comments
(
    id      INT AUTO_INCREMENT PRIMARY KEY,
    text    VARCHAR(255),
    item_id INT,
    user_id INT,
    created DATE,

    CONSTRAINT fk_item_for_comments FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_user_for_comments FOREIGN KEY (user_id) REFERENCES users (id)
);