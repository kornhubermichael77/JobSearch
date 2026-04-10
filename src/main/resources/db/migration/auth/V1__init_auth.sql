-- =========================================
-- AUTH DATABASE INITIAL SCHEMA (V1)
-- Basierend auf dem bestehenden jobsearch_users-Schema
-- =========================================

-- -----------------------------------------
-- Tabelle: roles
-- -----------------------------------------
CREATE TABLE roles (
                       role_id_PK BIGINT NOT NULL AUTO_INCREMENT,
                       role_name VARCHAR(255) NOT NULL,
                       PRIMARY KEY (role_id_PK),
                       CONSTRAINT uk_roles_role_name UNIQUE (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- -----------------------------------------
-- Tabelle: users
-- -----------------------------------------
CREATE TABLE users (
                       user_id_PK BIGINT NOT NULL AUTO_INCREMENT,
                       email VARCHAR(255) NOT NULL,
                       enabled BIT(1) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       tenant_db_name VARCHAR(255) NOT NULL,
                       user_name VARCHAR(255) NOT NULL,
                       PRIMARY KEY (user_id_PK),
                       CONSTRAINT uk_users_email UNIQUE (email),
                       CONSTRAINT uk_users_tenant_db_name UNIQUE (tenant_db_name),
                       CONSTRAINT uk_users_user_name UNIQUE (user_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- -----------------------------------------
-- Tabelle: user_roles
-- -----------------------------------------
CREATE TABLE user_roles (
                            user_id_FK BIGINT NOT NULL,
                            role_id_FK BIGINT NOT NULL,
                            PRIMARY KEY (user_id_FK, role_id_FK),
                            KEY idx_user_roles_role (role_id_FK),
                            CONSTRAINT fk_user_roles_user
                                FOREIGN KEY (user_id_FK) REFERENCES users (user_id_PK),
                            CONSTRAINT fk_user_roles_role
                                FOREIGN KEY (role_id_FK) REFERENCES roles (role_id_PK)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- -----------------------------------------
-- Tabelle: password_reset_token
-- -----------------------------------------
CREATE TABLE password_reset_token (
                                      id BIGINT NOT NULL AUTO_INCREMENT,
                                      user_id_fk BIGINT NOT NULL,
                                      token_hash VARCHAR(64) NOT NULL,
                                      expires_at DATETIME NOT NULL,
                                      used_at DATETIME DEFAULT NULL,
                                      created_at DATETIME NOT NULL,
                                      PRIMARY KEY (id),
                                      KEY idx_password_reset_user (user_id_fk),
                                      KEY idx_password_reset_token_hash (token_hash),
                                      CONSTRAINT fk_password_reset_user
                                          FOREIGN KEY (user_id_fk) REFERENCES users (user_id_PK)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =========================================
-- BASISDATEN
-- Ersetzt den bisherigen DataInitializer
-- =========================================

INSERT INTO roles (role_name) VALUES ('ROLE_USER');
INSERT INTO roles (role_name) VALUES ('ROLE_ADMIN');