CREATE TABLE company (
                         id_pk INT NOT NULL AUTO_INCREMENT,
                         mail VARCHAR(150) DEFAULT NULL,
                         mail_person VARCHAR(100) DEFAULT NULL,
                         name VARCHAR(100) DEFAULT NULL,
                         summary VARCHAR(5000) DEFAULT NULL,
                         tel VARCHAR(25) DEFAULT NULL,
                         tel_person VARCHAR(100) DEFAULT NULL,
                         url VARCHAR(500) DEFAULT NULL,
                         url_jobs VARCHAR(500) DEFAULT NULL,
                         PRIMARY KEY (id_pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE address (
                         id_pk INT NOT NULL AUTO_INCREMENT,
                         city VARCHAR(60) DEFAULT NULL,
                         country VARCHAR(50) DEFAULT NULL,
                         distance DOUBLE DEFAULT NULL,
                         headquarter BIT(1) DEFAULT NULL,
                         number VARCHAR(30) DEFAULT NULL,
                         postcode VARCHAR(10) DEFAULT NULL,
                         street VARCHAR(100) DEFAULT NULL,
                         traveltime DATETIME(6) DEFAULT NULL,
                         c_id_fk INT DEFAULT NULL,
                         owner_user_id BIGINT DEFAULT NULL,
                         PRIMARY KEY (id_pk),
                         KEY idx_address_company (c_id_fk),
                         KEY idx_address_owner_user (owner_user_id),
                         CONSTRAINT fk_address_company
                             FOREIGN KEY (c_id_fk) REFERENCES company (id_pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE job (
                     id_pk INT NOT NULL AUTO_INCREMENT,
                     features VARCHAR(2500) DEFAULT NULL,
                     found DATETIME(6) DEFAULT NULL,
                     gleitzeit VARCHAR(100) DEFAULT NULL,
                     homeoffice VARCHAR(100) DEFAULT NULL,
                     mail VARCHAR(150) DEFAULT NULL,
                     mail_person VARCHAR(100) DEFAULT NULL,
                     source VARCHAR(150) DEFAULT NULL,
                     status VARCHAR(50) DEFAULT NULL,
                     teilzeit VARCHAR(100) DEFAULT NULL,
                     tel VARCHAR(25) DEFAULT NULL,
                     tel_person VARCHAR(100) DEFAULT NULL,
                     text VARCHAR(10000) DEFAULT NULL,
                     url VARCHAR(500) DEFAULT NULL,
                     a_id_fk INT DEFAULT NULL,
                     c_id_fk INT DEFAULT NULL,
                     PRIMARY KEY (id_pk),
                     KEY idx_job_address (a_id_fk),
                     KEY idx_job_company (c_id_fk),
                     CONSTRAINT fk_job_address
                         FOREIGN KEY (a_id_fk) REFERENCES address (id_pk),
                     CONSTRAINT fk_job_company
                         FOREIGN KEY (c_id_fk) REFERENCES company (id_pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE communication (
                               id_pk INT NOT NULL AUTO_INCREMENT,
                               content VARCHAR(10000) DEFAULT NULL,
                               date DATETIME(6) DEFAULT NULL,
                               person VARCHAR(100) DEFAULT NULL,
                               role VARCHAR(100) DEFAULT NULL,
                               sidemarks VARCHAR(2500) DEFAULT NULL,
                               status VARCHAR(50) DEFAULT NULL,
                               job_id_fk INT DEFAULT NULL,
                               PRIMARY KEY (id_pk),
                               KEY idx_communication_job (job_id_fk),
                               CONSTRAINT fk_communication_job
                                   FOREIGN KEY (job_id_fk) REFERENCES job (id_pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE interview (
                           comm_id_pk INT NOT NULL,
                           conclusion VARCHAR(2500) DEFAULT NULL,
                           duration VARCHAR(150) DEFAULT NULL,
                           PRIMARY KEY (comm_id_pk),
                           CONSTRAINT fk_interview_communication
                               FOREIGN KEY (comm_id_pk) REFERENCES communication (id_pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE mail (
                      comm_id_pk INT NOT NULL,
                      address VARCHAR(150) DEFAULT NULL,
                      attachments VARCHAR(500) DEFAULT NULL,
                      subject VARCHAR(100) DEFAULT NULL,
                      direction ENUM('IN', 'OUT') NOT NULL,
                      PRIMARY KEY (comm_id_pk),
                      CONSTRAINT fk_mail_communication
                          FOREIGN KEY (comm_id_pk) REFERENCES communication (id_pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE phone (
                       comm_id_pk INT NOT NULL,
                       direction ENUM('IN', 'OUT') NOT NULL,
                       number VARCHAR(25) DEFAULT NULL,
                       PRIMARY KEY (comm_id_pk),
                       CONSTRAINT fk_phone_communication
                           FOREIGN KEY (comm_id_pk) REFERENCES communication (id_pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE talk (
                      comm_id_pk INT NOT NULL,
                      context VARCHAR(100) DEFAULT NULL,
                      location VARCHAR(100) DEFAULT NULL,
                      PRIMARY KEY (comm_id_pk),
                      CONSTRAINT fk_talk_communication
                          FOREIGN KEY (comm_id_pk) REFERENCES communication (id_pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE trial (
                       comm_id_pk INT NOT NULL,
                       conclusion VARCHAR(2500) DEFAULT NULL,
                       duration VARCHAR(150) DEFAULT NULL,
                       PRIMARY KEY (comm_id_pk),
                       CONSTRAINT fk_trial_communication
                           FOREIGN KEY (comm_id_pk) REFERENCES communication (id_pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE webform (
                         comm_id_pk INT NOT NULL,
                         screenshot VARCHAR(150) DEFAULT NULL,
                         url VARCHAR(500) DEFAULT NULL,
                         PRIMARY KEY (comm_id_pk),
                         CONSTRAINT fk_webform_communication
                             FOREIGN KEY (comm_id_pk) REFERENCES communication (id_pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;