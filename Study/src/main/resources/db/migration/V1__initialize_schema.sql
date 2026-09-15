CREATE TABLE IF NOT EXISTS role (
    role_id BIGINT NOT NULL AUTO_INCREMENT,
    role_name ENUM ('Tenant', 'Landlord', 'Admin'),
    PRIMARY KEY (role_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    deleted BIT NOT NULL DEFAULT b'0',
    created_at DATETIME(6),
    updated_at DATETIME(6),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    fullname VARCHAR(100) NOT NULL,
    tel VARCHAR(255),
    email VARCHAR(255),
    role_id BIGINT NOT NULL,
    link_avatar VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT uk_user_username UNIQUE (username)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS room (
    id BIGINT NOT NULL AUTO_INCREMENT,
    deleted BIT NOT NULL DEFAULT b'0',
    created_at DATETIME(6),
    updated_at DATETIME(6),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    user_id BIGINT NOT NULL,
    address VARCHAR(255),
    capacity BIGINT NOT NULL,
    price DOUBLE NOT NULL,
    description VARCHAR(5000),
    room_type VARCHAR(255),
    area DOUBLE NOT NULL,
    is_approval VARCHAR(255),
    image VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT chk_room_price CHECK (price >= 1)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS image (
    id BIGINT NOT NULL AUTO_INCREMENT,
    room_id BIGINT,
    url VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS appointment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    deleted BIT NOT NULL DEFAULT b'0',
    created_at DATETIME(6),
    updated_at DATETIME(6),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    username VARCHAR(255) NOT NULL,
    fullname VARCHAR(100) NOT NULL,
    tel VARCHAR(15) NOT NULL,
    email VARCHAR(50) NOT NULL,
    num_people INT NOT NULL,
    come_date DATETIME(6),
    transportation VARCHAR(50) NOT NULL,
    room_id BIGINT NOT NULL,
    is_approval VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_appointment_people CHECK (num_people >= 1)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS comment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255),
    avatar VARCHAR(255),
    content VARCHAR(255),
    comment_time DATETIME(6),
    room_id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

INSERT INTO role (role_id, role_name)
VALUES (1, 'Tenant'), (2, 'Landlord'), (3, 'Admin')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);
