-- =========================================
-- 테스트용 고양이 1마리 생성
-- 이미 있으면 count 값을 최신 값으로 수정
-- =========================================
INSERT INTO cats (
    id,                         -- 고양이 PK
    name,                       -- 고양이 이름
    birth_date,                 -- 생일
    diagnosed_date,             -- 당뇨 진단일
    invite_code,                -- 초대 코드
    meal_count,                 -- 식사 횟수
    blood_sugar_count,          -- 혈당 측정 횟수
    insulin_count               -- 인슐린 횟수
)
VALUES (
           '11111111-1111-1111-1111-111111111111', -- 테스트용 고정 UUID
           '나비',                                  -- 고양이 이름
           '2020-03-15',                            -- 생일
           '2024-01-10',                            -- 진단일
           'TEST-CAT-001',                          -- 테스트 초대 코드
           3,                                       -- 하루 식사 3회
           3,                                       -- 하루 혈당 3회
           3                                        -- 하루 인슐린 3회
       )
ON CONFLICT (id)
    DO UPDATE SET
                  meal_count = EXCLUDED.meal_count,
                  blood_sugar_count = EXCLUDED.blood_sugar_count,
                  insulin_count = EXCLUDED.insulin_count,
                  updated_at = CURRENT_TIMESTAMP;


-- =========================================
-- 이미 존재하는 유저에게 cat_id 연결
-- =========================================
UPDATE users
SET
    cat_id = '11111111-1111-1111-1111-111111111111',
    onboarding_completed = TRUE,
    updated_at = CURRENT_TIMESTAMP
WHERE provider = 'APPLE'
  AND provider_id = '000890.3b11da8f544b4c01978c4d392a4bd6c9.0409';


-- =========================================
-- 고양이 케어 스케줄 생성
-- 식사 3회, 혈당 3회, 인슐린 3회
-- =========================================

-- 식사 스케줄 1: 08:00
INSERT INTO care_schedules (
    id,
    cat_id,
    schedule_type,
    sequence,
    scheduled_time
)
VALUES (
           '22222222-2222-2222-2222-222222222221',
           '11111111-1111-1111-1111-111111111111',
           'MEAL',
           1,
           '08:00:00'
       )
ON CONFLICT (cat_id, schedule_type, sequence)
    DO UPDATE SET
                  scheduled_time = EXCLUDED.scheduled_time,
                  updated_at = CURRENT_TIMESTAMP;


-- 식사 스케줄 2: 13:00
INSERT INTO care_schedules (
    id,
    cat_id,
    schedule_type,
    sequence,
    scheduled_time
)
VALUES (
           '22222222-2222-2222-2222-222222222222',
           '11111111-1111-1111-1111-111111111111',
           'MEAL',
           2,
           '13:00:00'
       )
ON CONFLICT (cat_id, schedule_type, sequence)
    DO UPDATE SET
                  scheduled_time = EXCLUDED.scheduled_time,
                  updated_at = CURRENT_TIMESTAMP;


-- 식사 스케줄 3: 20:00
INSERT INTO care_schedules (
    id,
    cat_id,
    schedule_type,
    sequence,
    scheduled_time
)
VALUES (
           '22222222-2222-2222-2222-222222222223',
           '11111111-1111-1111-1111-111111111111',
           'MEAL',
           3,
           '20:00:00'
       )
ON CONFLICT (cat_id, schedule_type, sequence)
    DO UPDATE SET
                  scheduled_time = EXCLUDED.scheduled_time,
                  updated_at = CURRENT_TIMESTAMP;


-- 혈당 스케줄 1: 08:30
INSERT INTO care_schedules (
    id,
    cat_id,
    schedule_type,
    sequence,
    scheduled_time
)
VALUES (
           '33333333-3333-3333-3333-333333333331',
           '11111111-1111-1111-1111-111111111111',
           'BLOOD_SUGAR',
           1,
           '08:30:00'
       )
ON CONFLICT (cat_id, schedule_type, sequence)
    DO UPDATE SET
                  scheduled_time = EXCLUDED.scheduled_time,
                  updated_at = CURRENT_TIMESTAMP;


-- 혈당 스케줄 2: 13:30
INSERT INTO care_schedules (
    id,
    cat_id,
    schedule_type,
    sequence,
    scheduled_time
)
VALUES (
           '33333333-3333-3333-3333-333333333332',
           '11111111-1111-1111-1111-111111111111',
           'BLOOD_SUGAR',
           2,
           '13:30:00'
       )
ON CONFLICT (cat_id, schedule_type, sequence)
    DO UPDATE SET
                  scheduled_time = EXCLUDED.scheduled_time,
                  updated_at = CURRENT_TIMESTAMP;


-- 혈당 스케줄 3: 19:30
INSERT INTO care_schedules (
    id,
    cat_id,
    schedule_type,
    sequence,
    scheduled_time
)
VALUES (
           '33333333-3333-3333-3333-333333333333',
           '11111111-1111-1111-1111-111111111111',
           'BLOOD_SUGAR',
           3,
           '19:30:00'
       )
ON CONFLICT (cat_id, schedule_type, sequence)
    DO UPDATE SET
                  scheduled_time = EXCLUDED.scheduled_time,
                  updated_at = CURRENT_TIMESTAMP;


-- 인슐린 스케줄 1: 09:10
INSERT INTO care_schedules (
    id,
    cat_id,
    schedule_type,
    sequence,
    scheduled_time
)
VALUES (
           '44444444-4444-4444-4444-444444444441',
           '11111111-1111-1111-1111-111111111111',
           'INSULIN',
           1,
           '09:10:00'
       )
ON CONFLICT (cat_id, schedule_type, sequence)
    DO UPDATE SET
                  scheduled_time = EXCLUDED.scheduled_time,
                  updated_at = CURRENT_TIMESTAMP;


-- 인슐린 스케줄 2: 15:10
INSERT INTO care_schedules (
    id,
    cat_id,
    schedule_type,
    sequence,
    scheduled_time
)
VALUES (
           '44444444-4444-4444-4444-444444444442',
           '11111111-1111-1111-1111-111111111111',
           'INSULIN',
           2,
           '15:10:00'
       )
ON CONFLICT (cat_id, schedule_type, sequence)
    DO UPDATE SET
                  scheduled_time = EXCLUDED.scheduled_time,
                  updated_at = CURRENT_TIMESTAMP;


-- 인슐린 스케줄 3: 21:10
INSERT INTO care_schedules (
    id,
    cat_id,
    schedule_type,
    sequence,
    scheduled_time
)
VALUES (
           '44444444-4444-4444-4444-444444444443',
           '11111111-1111-1111-1111-111111111111',
           'INSULIN',
           3,
           '21:10:00'
       )
ON CONFLICT (cat_id, schedule_type, sequence)
    DO UPDATE SET
                  scheduled_time = EXCLUDED.scheduled_time,
                  updated_at = CURRENT_TIMESTAMP;


-- =========================================
-- 기존 인슐린 기록 삭제
-- 인슐린 기록 테스트를 깨끗하게 다시 하기 위함
-- =========================================
DELETE FROM insulin_records
WHERE cat_id = '11111111-1111-1111-1111-111111111111';