-- =============================================
-- WorkIt 초기 데이터 (data.sql)
-- ddl-auto: update 이후 자동 실행됨
-- ON DUPLICATE KEY UPDATE → 재배포해도 에러 없음
-- =============================================

-- =============================================
-- 1. locations (호주 주요 도시)
-- =============================================
INSERT INTO locations (name, city, country, latitude, longitude)
VALUES ('Sydney', 'Sydney', 'Australia', -33.86880000, 151.20930000)
    ON DUPLICATE KEY UPDATE city = VALUES(city);

INSERT INTO locations (name, city, country, latitude, longitude)
VALUES ('Melbourne', 'Melbourne', 'Australia', -37.81360000, 144.96310000)
    ON DUPLICATE KEY UPDATE city = VALUES(city);

INSERT INTO locations (name, city, country, latitude, longitude)
VALUES ('Brisbane', 'Brisbane', 'Australia', -27.46980000, 153.02510000)
    ON DUPLICATE KEY UPDATE city = VALUES(city);

INSERT INTO locations (name, city, country, latitude, longitude)
VALUES ('Perth', 'Perth', 'Australia', -31.95050000, 115.86050000)
    ON DUPLICATE KEY UPDATE city = VALUES(city);

INSERT INTO locations (name, city, country, latitude, longitude)
VALUES ('Adelaide', 'Adelaide', 'Australia', -34.92850000, 138.60070000)
    ON DUPLICATE KEY UPDATE city = VALUES(city);

INSERT INTO locations (name, city, country, latitude, longitude)
VALUES ('Gold Coast', 'Gold Coast', 'Australia', -28.01670000, 153.43330000)
    ON DUPLICATE KEY UPDATE city = VALUES(city);

INSERT INTO locations (name, city, country, latitude, longitude)
VALUES ('Canberra', 'Canberra', 'Australia', -35.28050000, 149.12910000)
    ON DUPLICATE KEY UPDATE city = VALUES(city);

INSERT INTO locations (name, city, country, latitude, longitude)
VALUES ('Hobart', 'Hobart', 'Australia', -42.88330000, 147.32170000)
    ON DUPLICATE KEY UPDATE city = VALUES(city);

INSERT INTO locations (name, city, country, latitude, longitude)
VALUES ('Darwin', 'Darwin', 'Australia', -12.46340000, 130.84560000)
    ON DUPLICATE KEY UPDATE city = VALUES(city);

INSERT INTO locations (name, city, country, latitude, longitude)
VALUES ('Cairns', 'Cairns', 'Australia', -16.90770000, 145.77520000)
    ON DUPLICATE KEY UPDATE city = VALUES(city);

-- =============================================
-- 2. categories (직종 카테고리)
-- =============================================
INSERT INTO categories (name) VALUES ('IT / Development')
    ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO categories (name) VALUES ('Design')
    ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO categories (name) VALUES ('Marketing')
    ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO categories (name) VALUES ('Finance / Accounting')
    ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO categories (name) VALUES ('Sales')
    ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO categories (name) VALUES ('Education')
    ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO categories (name) VALUES ('Healthcare')
    ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO categories (name) VALUES ('Hospitality')
    ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO categories (name) VALUES ('Logistics')
    ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO categories (name) VALUES ('Customer Service')
    ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO categories (name) VALUES ('HR / Recruitment')
    ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO categories (name) VALUES ('Engineering')
    ON DUPLICATE KEY UPDATE name = VALUES(name);

-- =============================================
-- 3. companies (샘플 회사)
-- =============================================
INSERT INTO companies (name, description, logo_url, website_url, created_at, updated_at)
VALUES ('TechNova Australia', 'Melbourne 기반 IT 솔루션 회사. 클라우드와 AI 분야에서 활발하게 활동 중입니다.', NULL, 'https://technova.com.au', NOW(), NOW())
    ON DUPLICATE KEY UPDATE description = VALUES(description);

INSERT INTO companies (name, description, logo_url, website_url, created_at, updated_at)
VALUES ('GreenLeaf Consulting', 'Sydney 기반 비즈니스 컨설팅 및 마케팅 전문사무소입니다.', NULL, 'https://greenleaf.com.au', NOW(), NOW())
    ON DUPLICATE KEY UPDATE description = VALUES(description);

INSERT INTO companies (name, description, logo_url, website_url, created_at, updated_at)
VALUES ('BlueSky Hospitality', 'Gold Coast와 Sydney에서 운영하는 호스피탈리티 그룹입니다.', NULL, 'https://bluesky-hospitality.com.au', NOW(), NOW())
    ON DUPLICATE KEY UPDATE description = VALUES(description);

INSERT INTO companies (name, description, logo_url, website_url, created_at, updated_at)
VALUES ('Aussie Finance Group', 'Perth 기반 금융 및 회계 서비스를 제공하는 회사입니다.', NULL, 'https://aussiefinance.com.au', NOW(), NOW())
    ON DUPLICATE KEY UPDATE description = VALUES(description);

INSERT INTO companies (name, description, logo_url, website_url, created_at, updated_at)
VALUES ('NextGen Education', 'Brisbane와 Canberra에서 온라인·오프라인 교육 프로그램을 운영합니다.', NULL, 'https://nextgen-edu.com.au', NOW(), NOW())
    ON DUPLICATE KEY UPDATE description = VALUES(description);

INSERT INTO companies (name, description, logo_url, website_url, created_at, updated_at)
VALUES ('PacificHealth Clinic', 'Melbourne 기반 종합 의료 클리닉입니다.', NULL, 'https://pacifichealth.com.au', NOW(), NOW())
    ON DUPLICATE KEY UPDATE description = VALUES(description);

INSERT INTO companies (name, description, logo_url, website_url, created_at, updated_at)
VALUES ('SwiftMove Logistics', 'Adelaide와 Darwin을 잇는 물류 및 배송 전문사업체입니다.', NULL, 'https://swiftmove.com.au', NOW(), NOW())
    ON DUPLICATE KEY UPDATE description = VALUES(description);

INSERT INTO companies (name, description, logo_url, website_url, created_at, updated_at)
VALUES ('DesignFlow Studio', 'Sydney 기반 UI/UX 및 그래픽 디자인 전문사무소입니다.', NULL, 'https://designflow.com.au', NOW(), NOW())
    ON DUPLICATE KEY UPDATE description = VALUES(description);

-- =============================================
-- 4. job_postings (샘플 공고)
-- company_id, location_id는 위에서 INSERT한 순서대로 1부터 매칭
--   companies: 1=TechNova, 2=GreenLeaf, 3=BlueSky, 4=Aussie Finance,
--              5=NextGen, 6=PacificHealth, 7=SwiftMove, 8=DesignFlow
--   locations: 1=Sydney, 2=Melbourne, 3=Brisbane, 4=Perth,
--              5=Adelaide, 6=Gold Coast, 7=Canberra, 8=Hobart, 9=Darwin, 10=Cairns
-- =============================================

-- 공고 1: TechNova - Sydney - Full-time - Long-term - HOT
INSERT INTO job_postings (title, description, company_id, location_id, compensation_amount, compensation_type, job_type, duration_type, image_url, is_hot, published_at, expires_at, created_at, updated_at)
VALUES (
           'Senior Backend Developer',
           'TechNova Australia에서 백엔드 개발자를 모집합니다.\n\n■ 우대 사항\n- Java / Spring Boot 경험 2년 이상\n- AWS 또는 클라우드 플랫폼 사용 경험\n- RESTful API 설계 경험\n\n■ 복리후생\n- 건강보험 지원\n- 유연 근무 제공\n- 연간 학습 수당',
           1, 1, 120000.00, 'YEARLY', 'FULL_TIME', 'LONG_TERM', NULL, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW()
       )
    ON DUPLICATE KEY UPDATE title = VALUES(title);

-- 공고 2: GreenLeaf - Sydney - Part-time - Short-term
INSERT INTO job_postings (title, description, company_id, location_id, compensation_amount, compensation_type, job_type, duration_type, image_url, is_hot, published_at, expires_at, created_at, updated_at)
VALUES (
           'Marketing Coordinator',
           'GreenLeaf Consulting에서 마케팅 팀원을 모집합니다.\n\n■ 업무내용\n- SNS 콘텐츠 제작 및 관리\n- 이벤트 기획 및 실행\n- 시장 리서치\n\n■ 우대 사항\n- 디지털 마케팅 기본 지식\n- 영어·한국어 이중어능력',
           2, 1, 35.00, 'HOURLY', 'PART_TIME', 'SHORT_TERM', NULL, FALSE, NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), NOW(), NOW()
       )
    ON DUPLICATE KEY UPDATE title = VALUES(title);

-- 공고 3: TechNova - Melbourne - Full-time - Long-term - HOT
INSERT INTO job_postings (title, description, company_id, location_id, compensation_amount, compensation_type, job_type, duration_type, image_url, is_hot, published_at, expires_at, created_at, updated_at)
VALUES (
           'Cloud Infrastructure Engineer',
           'TechNova Melbourne 오피스에서 클라우드 엔지니어를 모집합니다.\n\n■ 우대 사항\n- AWS Solutions Architect 인증 보유\n- Kubernetes / Docker 경험\n- CI/CD 파이프라인 구축 경험\n\n■ 복리후생\n- 주식 옵션 제공\n- relocation 지원',
           1, 2, 130000.00, 'YEARLY', 'FULL_TIME', 'LONG_TERM', NULL, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW()
       )
    ON DUPLICATE KEY UPDATE title = VALUES(title);

-- 공고 4: BlueSky - Gold Coast - Casual - Short-term
INSERT INTO job_postings (title, description, company_id, location_id, compensation_amount, compensation_type, job_type, duration_type, image_url, is_hot, published_at, expires_at, created_at, updated_at)
VALUES (
           'Hotel Front Desk Agent',
           'BlueSky Hospitality Gold Coast에서 프론트 데스크 직원을 모집합니다.\n\n■ 업무내용\n- 투숙객 체크인·체크아웃 처리\n- 예약 관리 및 문의 대응\n- 호텔 시설 안내\n\n■ 우대 사항\n- 영어 커뮤니케이션 가능\n- 서비스 업종 경험 보유',
           3, 6, 28.00, 'HOURLY', 'CASUAL', 'SHORT_TERM', NULL, FALSE, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), NOW(), NOW()
       )
    ON DUPLICATE KEY UPDATE title = VALUES(title);

-- 공고 5: Aussie Finance - Perth - Full-time - Long-term
INSERT INTO job_postings (title, description, company_id, location_id, compensation_amount, compensation_type, job_type, duration_type, image_url, is_hot, published_at, expires_at, created_at, updated_at)
VALUES (
           'Financial Analyst',
           'Aussie Finance Group에서 재무 분석가를 모집합니다.\n\n■ 우대 사항\n- CPA 또는 회계 관련 학위\n- Excel / Power BI 능숙\n- 금융 모델링 경험\n\n■ 복리후생\n- 성과 기반 보너스\n- 전문 교육 지원',
           4, 4, 95000.00, 'YEARLY', 'FULL_TIME', 'LONG_TERM', NULL, FALSE, NOW(), DATE_ADD(NOW(), INTERVAL 21 DAY), NOW(), NOW()
       )
    ON DUPLICATE KEY UPDATE title = VALUES(title);

-- 공고 6: NextGen - Brisbane - Part-time - Short-term - HOT
INSERT INTO job_postings (title, description, company_id, location_id, compensation_amount, compensation_type, job_type, duration_type, image_url, is_hot, published_at, expires_at, created_at, updated_at)
VALUES (
           'Online Tutor - Korean & English',
           'NextGen Education에서 온라인 튜터를 모집합니다.\n\n■ 업무내용\n- 한국어·영어 학습자 지도\n- 커리큘럼 설계 및 강의 진행\n\n■ 우대 사항\n- 교원 자격증 또는 교육 관련 학위\n- 온라인 강의 경험 보유',
           5, 3, 40.00, 'HOURLY', 'PART_TIME', 'SHORT_TERM', NULL, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), NOW(), NOW()
       )
    ON DUPLICATE KEY UPDATE title = VALUES(title);

-- 공고 7: PacificHealth - Melbourne - Full-time - Long-term
INSERT INTO job_postings (title, description, company_id, location_id, compensation_amount, compensation_type, job_type, duration_type, image_url, is_hot, published_at, expires_at, created_at, updated_at)
VALUES (
           'Registered Nurse',
           'PacificHealth Clinic에서 간호사를 모집합니다.\n\n■ 우대 사항\n- 호주 간호사 면허 보유 (AHPRA)\n- 임상 경험 2년 이상\n- 영어 커뮤니케이션 가능\n\n■ 복리후생\n- 건강보험 전액 지원\n- 근무 수당 별도 지급',
           6, 2, 110000.00, 'YEARLY', 'FULL_TIME', 'LONG_TERM', NULL, FALSE, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW()
       )
    ON DUPLICATE KEY UPDATE title = VALUES(title);

-- 공고 8: SwiftMove - Adelaide - Casual - Short-term
INSERT INTO job_postings (title, description, company_id, location_id, compensation_amount, compensation_type, job_type, duration_type, image_url, is_hot, published_at, expires_at, created_at, updated_at)
VALUES (
           'Delivery Driver',
           'SwiftMove Logistics에서 배송 드라이버를 모집합니다.\n\n■ 업무내용\n- 물류센터에서 배送 업무 수행\n- 경로 계획 및 시간 관리\n\n■ 우대 사항\n- 호주 운전면허 보유\n- 도로 운전 경험 1년 이상',
           7, 5, 30.00, 'HOURLY', 'CASUAL', 'SHORT_TERM', NULL, FALSE, NOW(), DATE_ADD(NOW(), INTERVAL 10 DAY), NOW(), NOW()
       )
    ON DUPLICATE KEY UPDATE title = VALUES(title);

-- 공고 9: DesignFlow - Sydney - Part-time - Long-term - HOT
INSERT INTO job_postings (title, description, company_id, location_id, compensation_amount, compensation_type, job_type, duration_type, image_url, is_hot, published_at, expires_at, created_at, updated_at)
VALUES (
           'UI/UX Designer',
           'DesignFlow Studio에서 UI/UX 디자이너를 모집합니다.\n\n■ 우대 사항\n- Figma 능숙\n- 포트폴리오 제출 필수\n- 모바일 앱 디자인 경험 보유\n\n■ 복리후생\n- 자유 근무 시간\n- 원격 근무 가능',
           8, 1, 55.00, 'HOURLY', 'PART_TIME', 'LONG_TERM', NULL, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 21 DAY), NOW(), NOW()
       )
    ON DUPLICATE KEY UPDATE title = VALUES(title);

-- 공고 10: BlueSky - Sydney - Full-time - Long-term
INSERT INTO job_postings (title, description, company_id, location_id, compensation_amount, compensation_type, job_type, duration_type, image_url, is_hot, published_at, expires_at, created_at, updated_at)
VALUES (
           'Restaurant Manager',
           'BlueSky Hospitality Sydney 레스토랑에서 매니저를 모집합니다.\n\n■ 업무내용\n- 매일 영업 운영 및 스탐프 관리\n- 메뉴 기획 및 품질 관리\n- 고객 만족도 향상\n\n■ 우대 사항\n- 외식업 관리 경험 3년 이상\n- 영어 커뮤니케이션 가능',
           3, 1, 85000.00, 'YEARLY', 'FULL_TIME', 'LONG_TERM', NULL, FALSE, NOW(), DATE_ADD(NOW(), INTERVAL 28 DAY), NOW(), NOW()
       )
    ON DUPLICATE KEY UPDATE title = VALUES(title);

-- =============================================
-- 5. job_posting_categories (공고 ↔ 카테고리 매핑)
-- categories: 1=IT/Dev, 2=Design, 3=Marketing, 4=Finance,
--             5=Sales, 6=Education, 7=Healthcare, 8=Hospitality,
--             9=Logistics, 10=Customer Service, 11=HR, 12=Engineering
-- job_postings: 1~10 (위에서 생성된 순서)
-- =============================================

-- 공고 1 (Senior Backend Developer) → IT / Development
INSERT INTO job_posting_categories (job_posting_id, category_id) VALUES (1, 1)
    ON DUPLICATE KEY UPDATE job_posting_id = VALUES(job_posting_id);

-- 공고 2 (Marketing Coordinator) → Marketing
INSERT INTO job_posting_categories (job_posting_id, category_id) VALUES (2, 3)
    ON DUPLICATE KEY UPDATE job_posting_id = VALUES(job_posting_id);

-- 공고 3 (Cloud Infrastructure Engineer) → IT / Development, Engineering
INSERT INTO job_posting_categories (job_posting_id, category_id) VALUES (3, 1)
    ON DUPLICATE KEY UPDATE job_posting_id = VALUES(job_posting_id);
INSERT INTO job_posting_categories (job_posting_id, category_id) VALUES (3, 12)
    ON DUPLICATE KEY UPDATE job_posting_id = VALUES(job_posting_id);

-- 공고 4 (Hotel Front Desk) → Hospitality, Customer Service
INSERT INTO job_posting_categories (job_posting_id, category_id) VALUES (4, 8)
    ON DUPLICATE KEY UPDATE job_posting_id = VALUES(job_posting_id);
INSERT INTO job_posting_categories (job_posting_id, category_id) VALUES (4, 10)
    ON DUPLICATE KEY UPDATE job_posting_id = VALUES(job_posting_id);

-- 공고 5 (Financial Analyst) → Finance / Accounting
INSERT INTO job_posting_categories (job_posting_id, category_id) VALUES (5, 4)
    ON DUPLICATE KEY UPDATE job_posting_id = VALUES(job_posting_id);

-- 공고 6 (Online Tutor) → Education
INSERT INTO job_posting_categories (job_posting_id, category_id) VALUES (6, 6)
    ON DUPLICATE KEY UPDATE job_posting_id = VALUES(job_posting_id);

-- 공고 7 (Registered Nurse) → Healthcare
INSERT INTO job_posting_categories (job_posting_id, category_id) VALUES (7, 7)
    ON DUPLICATE KEY UPDATE job_posting_id = VALUES(job_posting_id);

-- 공고 8 (Delivery Driver) → Logistics
INSERT INTO job_posting_categories (job_posting_id, category_id) VALUES (8, 9)
    ON DUPLICATE KEY UPDATE job_posting_id = VALUES(job_posting_id);

-- 공고 9 (UI/UX Designer) → Design
INSERT INTO job_posting_categories (job_posting_id, category_id) VALUES (9, 2)
    ON DUPLICATE KEY UPDATE job_posting_id = VALUES(job_posting_id);

-- 공고 10 (Restaurant Manager) → Hospitality
INSERT INTO job_posting_categories (job_posting_id, category_id) VALUES (10, 8)
    ON DUPLICATE KEY UPDATE job_posting_id = VALUES(job_posting_id);