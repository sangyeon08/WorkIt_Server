-- WorkIt MySQL initialization script for MySQL Workbench.
-- This creates the schema used by the Spring Boot backend and inserts seed data for Swagger/API checks.

CREATE DATABASE IF NOT EXISTS workit_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE workit_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS chat_messages;
DROP TABLE IF EXISTS chat_rooms;
DROP TABLE IF EXISTS recently_viewed_jobs;
DROP TABLE IF EXISTS bookmarks;
DROP TABLE IF EXISTS applications;
DROP TABLE IF EXISTS resumes;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS job_posting_categories;
DROP TABLE IF EXISTS job_postings;
DROP TABLE IF EXISTS companies;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS locations;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE locations (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  city VARCHAR(255),
  country VARCHAR(255),
  latitude DECIMAL(10, 8),
  longitude DECIMAL(11, 8),
  PRIMARY KEY (id),
  UNIQUE KEY uk_locations_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255),
  role VARCHAR(20),
  login_type VARCHAR(20),
  google_id VARCHAR(255),
  apple_id VARCHAR(255),
  location_id BIGINT,
  preferences JSON,
  created_at DATETIME(6),
  updated_at DATETIME(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_email (email),
  UNIQUE KEY uk_users_google_id (google_id),
  UNIQUE KEY uk_users_apple_id (apple_id),
  KEY idx_users_location_id (location_id),
  CONSTRAINT fk_users_location FOREIGN KEY (location_id) REFERENCES locations (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE categories (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_categories_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE companies (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  logo_url VARCHAR(512),
  website_url VARCHAR(512),
  created_at DATETIME(6),
  updated_at DATETIME(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_companies_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE job_postings (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  company_id BIGINT NOT NULL,
  location_id BIGINT NOT NULL,
  employer_id BIGINT,
  compensation_amount DECIMAL(12, 2),
  compensation_type VARCHAR(20),
  job_type VARCHAR(20) NOT NULL,
  duration_type VARCHAR(20),
  image_url VARCHAR(512),
  is_hot BOOLEAN NOT NULL DEFAULT FALSE,
  published_at DATETIME(6),
  expires_at DATETIME(6),
  created_at DATETIME(6),
  updated_at DATETIME(6),
  PRIMARY KEY (id),
  KEY idx_job_postings_company_id (company_id),
  KEY idx_job_postings_location_id (location_id),
  KEY idx_job_postings_employer_id (employer_id),
  KEY idx_job_postings_published_at (published_at),
  KEY idx_job_postings_is_hot (is_hot),
  CONSTRAINT fk_job_postings_company FOREIGN KEY (company_id) REFERENCES companies (id),
  CONSTRAINT fk_job_postings_location FOREIGN KEY (location_id) REFERENCES locations (id),
  CONSTRAINT fk_job_postings_employer FOREIGN KEY (employer_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE job_posting_categories (
  job_posting_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  PRIMARY KEY (job_posting_id, category_id),
  KEY idx_job_posting_categories_category_id (category_id),
  CONSTRAINT fk_job_posting_categories_job FOREIGN KEY (job_posting_id) REFERENCES job_postings (id) ON DELETE CASCADE,
  CONSTRAINT fk_job_posting_categories_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE bookmarks (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  job_posting_id BIGINT NOT NULL,
  created_at DATETIME(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_bookmarks_user_job (user_id, job_posting_id),
  KEY idx_bookmarks_job_posting_id (job_posting_id),
  CONSTRAINT fk_bookmarks_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_bookmarks_job FOREIGN KEY (job_posting_id) REFERENCES job_postings (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE recently_viewed_jobs (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  job_posting_id BIGINT NOT NULL,
  viewed_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_recently_viewed_user_job (user_id, job_posting_id),
  KEY idx_recently_viewed_job_posting_id (job_posting_id),
  KEY idx_recently_viewed_viewed_at (viewed_at),
  CONSTRAINT fk_recently_viewed_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_recently_viewed_job FOREIGN KEY (job_posting_id) REFERENCES job_postings (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE applications (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  job_posting_id BIGINT NOT NULL,
  cover_letter TEXT,
  phone VARCHAR(20),
  email VARCHAR(255),
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  applied_at DATETIME(6),
  updated_at DATETIME(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_applications_user_job (user_id, job_posting_id),
  KEY idx_applications_job_posting_id (job_posting_id),
  KEY idx_applications_status (status),
  CONSTRAINT fk_applications_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_applications_job FOREIGN KEY (job_posting_id) REFERENCES job_postings (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE resumes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  title VARCHAR(100),
  summary TEXT,
  experience TEXT,
  education TEXT,
  skills TEXT,
  certifications TEXT,
  resume_file_url VARCHAR(512),
  created_at DATETIME(6),
  updated_at DATETIME(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_resumes_user_id (user_id),
  CONSTRAINT fk_resumes_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE notifications (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  message VARCHAR(500) NOT NULL,
  type VARCHAR(20) NOT NULL,
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  created_at DATETIME(6),
  PRIMARY KEY (id),
  KEY idx_notifications_user_created (user_id, created_at),
  KEY idx_notifications_user_read (user_id, is_read),
  CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE chat_rooms (
  id BIGINT NOT NULL AUTO_INCREMENT,
  job_posting_id BIGINT NOT NULL,
  employer_id BIGINT NOT NULL,
  applicant_id BIGINT NOT NULL,
  created_at DATETIME(6),
  updated_at DATETIME(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_chat_rooms_job_applicant (job_posting_id, applicant_id),
  KEY idx_chat_rooms_employer_id (employer_id),
  KEY idx_chat_rooms_applicant_id (applicant_id),
  CONSTRAINT fk_chat_rooms_job FOREIGN KEY (job_posting_id) REFERENCES job_postings (id) ON DELETE CASCADE,
  CONSTRAINT fk_chat_rooms_employer FOREIGN KEY (employer_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_chat_rooms_applicant FOREIGN KEY (applicant_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE chat_messages (
  id BIGINT NOT NULL AUTO_INCREMENT,
  chat_room_id BIGINT NOT NULL,
  sender_id BIGINT NOT NULL,
  message TEXT NOT NULL,
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  created_at DATETIME(6),
  PRIMARY KEY (id),
  KEY idx_chat_messages_room_created (chat_room_id, created_at),
  KEY idx_chat_messages_sender_id (sender_id),
  KEY idx_chat_messages_read (is_read),
  CONSTRAINT fk_chat_messages_room FOREIGN KEY (chat_room_id) REFERENCES chat_rooms (id) ON DELETE CASCADE,
  CONSTRAINT fk_chat_messages_sender FOREIGN KEY (sender_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO locations (id, name, city, country, latitude, longitude) VALUES
  (1, 'Sydney', 'Sydney', 'Australia', -33.86880000, 151.20930000),
  (2, 'Melbourne', 'Melbourne', 'Australia', -37.81360000, 144.96310000),
  (3, 'Brisbane', 'Brisbane', 'Australia', -27.46980000, 153.02510000),
  (4, 'Perth', 'Perth', 'Australia', -31.95050000, 115.86050000),
  (5, 'Adelaide', 'Adelaide', 'Australia', -34.92850000, 138.60070000),
  (6, 'Gold Coast', 'Gold Coast', 'Australia', -28.01670000, 153.43330000),
  (7, 'Canberra', 'Canberra', 'Australia', -35.28050000, 149.12910000),
  (8, 'Hobart', 'Hobart', 'Australia', -42.88330000, 147.32170000),
  (9, 'Darwin', 'Darwin', 'Australia', -12.46340000, 130.84560000),
  (10, 'Cairns', 'Cairns', 'Australia', -16.90770000, 145.77520000);

INSERT INTO categories (id, name) VALUES
  (1, 'IT / Development'),
  (2, 'Design'),
  (3, 'Marketing'),
  (4, 'Finance / Accounting'),
  (5, 'Sales'),
  (6, 'Education'),
  (7, 'Healthcare'),
  (8, 'Hospitality'),
  (9, 'Logistics'),
  (10, 'Customer Service'),
  (11, 'HR / Recruitment'),
  (12, 'Engineering');

INSERT INTO companies (id, name, description, logo_url, website_url, created_at, updated_at) VALUES
  (1, 'TechNova Australia', 'Melbourne based IT solutions company focused on cloud and AI.', NULL, 'https://technova.com.au', NOW(6), NOW(6)),
  (2, 'GreenLeaf Consulting', 'Sydney based business consulting and marketing office.', NULL, 'https://greenleaf.com.au', NOW(6), NOW(6)),
  (3, 'BlueSky Hospitality', 'Hospitality group operating in Gold Coast and Sydney.', NULL, 'https://bluesky-hospitality.com.au', NOW(6), NOW(6)),
  (4, 'Aussie Finance Group', 'Perth based finance and accounting services company.', NULL, 'https://aussiefinance.com.au', NOW(6), NOW(6)),
  (5, 'NextGen Education', 'Online and offline education provider in Brisbane and Canberra.', NULL, 'https://nextgen-edu.com.au', NOW(6), NOW(6)),
  (6, 'PacificHealth Clinic', 'Melbourne based general healthcare clinic.', NULL, 'https://pacifichealth.com.au', NOW(6), NOW(6)),
  (7, 'SwiftMove Logistics', 'Logistics and delivery company connecting Adelaide and Darwin.', NULL, 'https://swiftmove.com.au', NOW(6), NOW(6)),
  (8, 'DesignFlow Studio', 'Sydney based UI/UX and graphic design studio.', NULL, 'https://designflow.com.au', NOW(6), NOW(6));

INSERT INTO users (id, email, password, role, login_type, location_id, preferences, created_at, updated_at) VALUES
  (1, 'employer@workit.test', NULL, 'EMPLOYER', 'LOCAL', 2, JSON_OBJECT('seed', true), NOW(6), NOW(6)),
  (2, 'seeker@workit.test', NULL, 'SEEKER', 'LOCAL', 2, JSON_OBJECT('seed', true), NOW(6), NOW(6));

INSERT INTO job_postings (id, title, description, company_id, location_id, employer_id, compensation_amount, compensation_type, job_type, duration_type, image_url, is_hot, published_at, expires_at, created_at, updated_at) VALUES
  (1, 'Senior Backend Developer', 'Java / Spring Boot backend role with cloud experience preferred.', 1, 1, 1, 120000.00, 'YEARLY', 'FULL_TIME', 'LONG_TERM', NULL, TRUE, NOW(6), DATE_ADD(NOW(6), INTERVAL 30 DAY), NOW(6), NOW(6)),
  (2, 'Marketing Coordinator', 'Create and manage SNS content, events, and market research.', 2, 1, 1, 35.00, 'HOURLY', 'PART_TIME', 'SHORT_TERM', NULL, FALSE, NOW(6), DATE_ADD(NOW(6), INTERVAL 14 DAY), NOW(6), NOW(6)),
  (3, 'Cloud Infrastructure Engineer', 'AWS, Kubernetes, Docker, and CI/CD pipeline role.', 1, 2, 1, 130000.00, 'YEARLY', 'FULL_TIME', 'LONG_TERM', NULL, TRUE, NOW(6), DATE_ADD(NOW(6), INTERVAL 30 DAY), NOW(6), NOW(6)),
  (4, 'Hotel Front Desk Agent', 'Guest check-in, reservation management, and customer support.', 3, 6, 1, 28.00, 'HOURLY', 'CASUAL', 'SHORT_TERM', NULL, FALSE, NOW(6), DATE_ADD(NOW(6), INTERVAL 7 DAY), NOW(6), NOW(6)),
  (5, 'Financial Analyst', 'Finance modeling, Excel, Power BI, and accounting analysis.', 4, 4, 1, 95000.00, 'YEARLY', 'FULL_TIME', 'LONG_TERM', NULL, FALSE, NOW(6), DATE_ADD(NOW(6), INTERVAL 21 DAY), NOW(6), NOW(6)),
  (6, 'Online Tutor - Korean & English', 'Online Korean and English tutoring role.', 5, 3, 1, 40.00, 'HOURLY', 'PART_TIME', 'SHORT_TERM', NULL, TRUE, NOW(6), DATE_ADD(NOW(6), INTERVAL 14 DAY), NOW(6), NOW(6)),
  (7, 'Registered Nurse', 'AHPRA registered nurse role at PacificHealth Clinic.', 6, 2, 1, 110000.00, 'YEARLY', 'FULL_TIME', 'LONG_TERM', NULL, FALSE, NOW(6), DATE_ADD(NOW(6), INTERVAL 30 DAY), NOW(6), NOW(6)),
  (8, 'Delivery Driver', 'Delivery route planning and logistics driving role.', 7, 5, 1, 30.00, 'HOURLY', 'CASUAL', 'SHORT_TERM', NULL, FALSE, NOW(6), DATE_ADD(NOW(6), INTERVAL 10 DAY), NOW(6), NOW(6)),
  (9, 'UI/UX Designer', 'Figma based mobile app UI/UX design role.', 8, 1, 1, 55.00, 'HOURLY', 'PART_TIME', 'LONG_TERM', NULL, TRUE, NOW(6), DATE_ADD(NOW(6), INTERVAL 21 DAY), NOW(6), NOW(6)),
  (10, 'Restaurant Manager', 'Restaurant operations, staff management, and customer satisfaction.', 3, 1, 1, 85000.00, 'YEARLY', 'FULL_TIME', 'LONG_TERM', NULL, FALSE, NOW(6), DATE_ADD(NOW(6), INTERVAL 28 DAY), NOW(6), NOW(6));

INSERT INTO job_posting_categories (job_posting_id, category_id) VALUES
  (1, 1), (2, 3), (3, 1), (3, 12), (4, 8), (4, 10),
  (5, 4), (6, 6), (7, 7), (8, 9), (9, 2), (10, 8);

INSERT INTO notifications (user_id, message, type, is_read, created_at) VALUES
  (2, 'Today''s recommended part-time jobs are ready.', 'JOB', FALSE, NOW(6)),
  (2, 'Your application status has been updated.', 'APPLICATION', FALSE, NOW(6));

INSERT INTO resumes (user_id, title, summary, skills, created_at, updated_at) VALUES
  (2, 'Default Resume', 'Sample seeker resume for API testing.', 'Communication, Customer Service', NOW(6), NOW(6));

INSERT INTO bookmarks (user_id, job_posting_id, created_at) VALUES
  (2, 1, NOW(6)),
  (2, 3, NOW(6));

INSERT INTO recently_viewed_jobs (user_id, job_posting_id, viewed_at) VALUES
  (2, 1, NOW(6)),
  (2, 4, NOW(6));

INSERT INTO applications (user_id, job_posting_id, cover_letter, phone, email, status, applied_at, updated_at) VALUES
  (2, 2, 'I am interested in this role.', '01000000000', 'seeker@workit.test', 'PENDING', NOW(6), NOW(6)),
  (2, 6, 'I have tutoring experience.', '01000000000', 'seeker@workit.test', 'ACCEPTED', NOW(6), NOW(6));

INSERT INTO chat_rooms (id, job_posting_id, employer_id, applicant_id, created_at, updated_at) VALUES
  (1, 2, 1, 2, NOW(6), NOW(6));

INSERT INTO chat_messages (chat_room_id, sender_id, message, is_read, created_at) VALUES
  (1, 1, 'Hello, thanks for applying.', FALSE, NOW(6)),
  (1, 2, 'Thank you for reaching out.', TRUE, NOW(6));
