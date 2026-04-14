-- ===========================
-- DROP TABLES
-- ===========================
DROP TABLE IF EXISTS blood_sugar_statistics;
DROP TABLE IF EXISTS insulin_records;
DROP TABLE IF EXISTS meal_records;
DROP TABLE IF EXISTS blood_sugar_records;
DROP TABLE IF EXISTS care_schedules;
DROP TABLE IF EXISTS cat_care_settings;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS cats;

-- ===========================
-- DROP ENUM TYPES
-- ===========================
DROP TYPE IF EXISTS period_type_enum;
DROP TYPE IF EXISTS meal_status_enum;
DROP TYPE IF EXISTS sugar_status_enum;
DROP TYPE IF EXISTS schedule_type_enum;
DROP TYPE IF EXISTS provider_type_enum;


-- =========================================
-- ENUM 타입 정의
-- =========================================
CREATE TYPE schedule_type_enum AS ENUM ('MEAL','BLOOD_SUGAR','INSULIN');
CREATE TYPE sugar_status_enum AS ENUM ('LOW','NORMAL','HIGH');
CREATE TYPE meal_status_enum AS ENUM ('FULL','PARTIAL');
CREATE TYPE period_type_enum AS ENUM ('WEEK','MONTH');
CREATE TYPE provider_type_enum AS ENUM ('KAKAO','APPLE');


-- =========================================
-- 1. cats
-- 고양이 기본 정보
-- 고양이 삭제시 관련 데이터 다 삭제됩니다. (다른 테이블 다)
-- =========================================
CREATE TABLE cats
(
    id             UUID PRIMARY KEY,
    name           VARCHAR(50) NOT NULL,
    birth_date     DATE, -- 예시) YYYY-MM-DD
    diagnosed_date DATE        NOT NULL,
    invite_code    VARCHAR(50) UNIQUE
);

-- =========================================
-- 2. users
-- 고양이를 함께 관리하는 사용자
-- 한 고양이를 여러 사용자가 함께 관리할 수 있음
-- 마지막 사용자 삭제시 고양이도 삭제되어야함 -> 서비스로직에서 구현
-- =========================================
CREATE TABLE users
(
    id                               UUID PRIMARY KEY,
    nickname                         VARCHAR(50) NOT NULL,
    cat_id                           UUID        NOT NULL,
    insulin_notification_enabled     BOOLEAN     NOT NULL DEFAULT FALSE,
    blood_sugar_notification_enabled BOOLEAN     NOT NULL DEFAULT FALSE,
    meal_notification_enabled        BOOLEAN     NOT NULL DEFAULT FALSE,
    provider                         VARCHAR(20) NOT NULL,
    provider_id                      VARCHAR(100) NOT NULL,
    refresh_token                    VARCHAR(500),
    refresh_token_expires_at         TIMESTAMPTZ,

    CONSTRAINT fk_users_cat
        FOREIGN KEY (cat_id) REFERENCES cats (id)
            ON DELETE CASCADE,

    CONSTRAINT uq_users_provider_provider_id
        UNIQUE (provider, provider_id)
);

-- =========================================
-- 3. cat_care_settings
-- 하루 기본 관리 횟수
-- 고양이 1마리당 설정 1개만 존재 -> cat_id UNIQUE 제약
-- =========================================
CREATE TABLE cat_care_settings
(
    id                UUID PRIMARY KEY,
    cat_id            UUID NOT NULL UNIQUE,
    meal_count        INT  NOT NULL CHECK (meal_count BETWEEN 1 AND 4),
    blood_sugar_count INT  NOT NULL CHECK (blood_sugar_count BETWEEN 1 AND 8),
    insulin_count     INT  NOT NULL CHECK (insulin_count BETWEEN 1 AND 3),

    CONSTRAINT fk_cat_care_settings_cat
        FOREIGN KEY (cat_id) REFERENCES cats (id)
            ON DELETE CASCADE
);

-- =========================================
-- 4. care_schedules
-- 반복 시간 설정
-- 시간 입력이 없는 경우 row가 생기지 않음!
-- =========================================
CREATE TABLE care_schedules
(
    id             UUID PRIMARY KEY,
    cat_id         UUID               NOT NULL,
    schedule_type  schedule_type_enum NOT NULL,
    sequence       INT                NOT NULL CHECK (sequence >= 1),
    scheduled_time TIME               NOT NULL,  -- 예시) HH:mm:ss

    CONSTRAINT fk_care_schedules_cat
        FOREIGN KEY (cat_id) REFERENCES cats (id)
            ON DELETE CASCADE,

    CONSTRAINT uq_care_schedules_cat_type_sequence
        UNIQUE (cat_id, schedule_type, sequence) -- 같은 고양이의 같은 타입에서 순번 중복 방지
);

-- =========================================
-- 5. blood_sugar_records
-- 혈당 기록
-- =========================================
CREATE TABLE blood_sugar_records
(
    id                  UUID PRIMARY KEY,
    cat_id              UUID              NOT NULL,
    recorded_by_user_id UUID,
    record_date         DATE              NOT NULL,
    record_time         TIME              NOT NULL,
    sequence            INT               NOT NULL CHECK (sequence >= 1),
    sugar_value         INT               NOT NULL CHECK (sugar_value >= 0),
    sugar_status        sugar_status_enum NOT NULL,

    CONSTRAINT fk_blood_sugar_records_cat
        FOREIGN KEY (cat_id) REFERENCES cats (id)
            ON DELETE CASCADE,                     -- 고양이 삭제시 관련 기록 삭제

    CONSTRAINT fk_blood_sugar_records_user
        FOREIGN KEY (recorded_by_user_id) REFERENCES users (id)
            ON DELETE SET NULL,                    -- 사용자 삭제시 recorded_by_user_id null로 처리

    CONSTRAINT uq_blood_sugar_records_cat_date_sequence
            UNIQUE (cat_id, record_date, sequence) -- 같은 날 같은 순번 중복 방지
);

-- =========================================
-- 6. meal_records
-- 식사 기록
-- =========================================
CREATE TABLE meal_records
(
    id                  UUID PRIMARY KEY,
    cat_id              UUID             NOT NULL,
    recorded_by_user_id UUID,
    record_date         DATE             NOT NULL,
    record_time         TIME             NOT NULL,
    sequence            INT              NOT NULL CHECK (sequence >= 1),
    meal_status         meal_status_enum NOT NULL,

    CONSTRAINT fk_meal_records_cat
        FOREIGN KEY (cat_id) REFERENCES cats (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_meal_records_user
        FOREIGN KEY (recorded_by_user_id) REFERENCES users (id)
            ON DELETE SET NULL,

    CONSTRAINT uq_meal_records_cat_date_sequence
        UNIQUE (cat_id, record_date, sequence)
);

-- =========================================
-- 7. insulin_records
-- 인슐린 투여 기록
-- =========================================
CREATE TABLE insulin_records
(
    id                  UUID PRIMARY KEY,
    cat_id              UUID    NOT NULL,
    recorded_by_user_id UUID,
    record_date         DATE    NOT NULL,
    sequence            INT     NOT NULL CHECK (sequence >= 1),
    is_injected         BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_insulin_records_cat
        FOREIGN KEY (cat_id) REFERENCES cats (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_insulin_records_user
        FOREIGN KEY (recorded_by_user_id) REFERENCES users (id)
            ON DELETE SET NULL,

    CONSTRAINT uq_insulin_records_cat_date_sequence
        UNIQUE (cat_id, record_date, sequence)
);

-- =========================================
-- 8. blood_sugar_statistics
-- 그래프용 주간 / 월간 집계 데이터
-- 추후에 수정 예정
-- =========================================
CREATE TABLE blood_sugar_statistics
(
    id           UUID PRIMARY KEY,
    cat_id       UUID             NOT NULL,
    period_type  period_type_enum NOT NULL,
    target_date  DATE             NOT NULL,
    avg_sugar    INT,
    max_sugar    INT,
    record_count INT              NOT NULL CHECK (record_count >= 0),

    CONSTRAINT fk_blood_sugar_statistics_cat
        FOREIGN KEY (cat_id) REFERENCES cats (id)
            ON DELETE CASCADE,

    CONSTRAINT uq_blood_sugar_statistics_cat_period_target
        UNIQUE (cat_id, period_type, target_date) -- 같은 기간 데이터 중복 저장 방지
);


-- =========================================
-- 인덱스 추가
-- =========================================
CREATE INDEX idx_users_cat_id ON users (cat_id);
CREATE INDEX idx_care_schedules_cat_id ON care_schedules (cat_id);
CREATE INDEX idx_blood_sugar_records_cat_date ON blood_sugar_records (cat_id, record_date);
CREATE INDEX idx_meal_records_cat_date ON meal_records (cat_id, record_date);
CREATE INDEX idx_insulin_records_cat_date ON insulin_records (cat_id, record_date);
CREATE INDEX idx_blood_sugar_statistics_cat_period_target ON blood_sugar_statistics (cat_id, period_type, target_date);