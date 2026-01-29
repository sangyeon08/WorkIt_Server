# 메인 화면 MySQL DB 테이블 구조

## ER 요약

- **users** (기존) + `location_id`, `preferences` → **locations**
- **companies** ─1:N─ **job_postings** ─N:M─ **categories** (`job_posting_categories`)
- **job_postings** → **locations**
- **users** ─1:N─ **notifications**

## 테이블

| 테이블 | 주요 컬럼 |
|--------|-----------|
| **locations** | id, name, city, country, latitude, longitude |
| **companies** | id, name, description, logo_url, website_url, created_at, updated_at |
| **job_postings** | id, title, description, company_id, location_id, compensation_amount, compensation_type, job_type, duration_type, image_url, is_hot, published_at, expires_at, created_at, updated_at |
| **categories** | id, name |
| **job_posting_categories** | job_posting_id, category_id |
| **notifications** | id, user_id, message, type, is_read, created_at |
| **users** (확장) | + location_id, preferences |

JPA `ddl-auto: update` 로 앱 기동 시 자동 반영됩니다.
