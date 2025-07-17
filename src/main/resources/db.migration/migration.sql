-- USER TABLE
CREATE TABLE `user` (
                        id int PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        email VARCHAR(100) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        token TEXT
);

-- TRANSACTION TABLE
CREATE TABLE `transaction` (
                               id int PRIMARY KEY,
                               user_id CHAR(36) NOT NULL,
                               amount VARCHAR(50) NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               CONSTRAINT fk_user_transaction FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE
);
