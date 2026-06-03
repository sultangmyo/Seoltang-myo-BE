package com.seoltangmyo.sugarcat.domain.cat.service;

import com.seoltangmyo.sugarcat.domain.cat.dto.CatCreateRequest;
import com.seoltangmyo.sugarcat.domain.cat.dto.CatInfoResponse;
import com.seoltangmyo.sugarcat.domain.cat.dto.CatInfoUpdateRequest;
import com.seoltangmyo.sugarcat.domain.cat.dto.InviteCodeResponse;
import com.seoltangmyo.sugarcat.domain.cat.dto.InviteCodeValidateResponse;
import com.seoltangmyo.sugarcat.domain.cat.entity.Cat;
import com.seoltangmyo.sugarcat.domain.cat.repository.CatRepository;
import com.seoltangmyo.sugarcat.domain.schedule.entity.CareSchedule;
import com.seoltangmyo.sugarcat.domain.schedule.entity.CareScheduleType;
import com.seoltangmyo.sugarcat.domain.schedule.repository.CareScheduleRepository;
import com.seoltangmyo.sugarcat.domain.user.dto.MessageResponse;
import com.seoltangmyo.sugarcat.domain.user.entity.User;
import com.seoltangmyo.sugarcat.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatService {

    private final CatRepository catRepository;
    private final CareScheduleRepository careScheduleRepository;
    private final UserRepository userRepository;

    // 스케줄 시간 파싱 포맷 (프론트에서 "HH:mm" 형식으로 전달)
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // 고양이 신규 등록
    // 1. 고양이 기본 정보 저장
    // 2. 현재 사용자의 catId를 새로 생성된 고양이로 연결
    // 3. 식사/혈당/인슐린 루틴 각각 저장 (schedules 빈 배열이면 해당 타입 row 미생성)
    @Transactional
    public MessageResponse createCat(UUID userId, CatCreateRequest request) {
        log.info("[고양이 등록] userId={}", userId);

        // 1. 고양이 생성 및 저장
        Cat cat = Cat.create(
                request.cat().name(),
                request.cat().birthDate(),
                request.cat().diagnosedDate(),
                request.cat().mealCount(),
                request.cat().bloodSugarCount(),
                request.cat().insulinCount()
        );
        catRepository.save(cat);

        // 2. 현재 사용자와 고양이 연결
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.assignCat(cat);

        // 3. 루틴 스케줄 저장
        // schedules가 빈 배열이면 for-each가 실행되지 않아 row 미생성
        saveSchedules(cat, CareScheduleType.MEAL, request.meal().schedules());
        saveSchedules(cat, CareScheduleType.BLOODSUGAR, request.bloodSugar().schedules());
        saveSchedules(cat, CareScheduleType.INSULIN, request.insulin().schedules());

        log.info("[고양이 등록 완료] catId={}, catName={}", cat.getId(), cat.getName());

        return new MessageResponse("고양이 기본 정보가 생성되었습니다.");
    }

    private static final String INVITE_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int INVITE_CODE_LENGTH = 8;

    // 초대코드 조회
    // GET /api/v1/cats/me/invite-code
    @Transactional
    public InviteCodeResponse getInviteCode(UUID userId) {
        log.info("[초대코드 조회] userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        log.info("[초대코드 조회 완료] catId={}", cat.getId());

        return new InviteCodeResponse(cat.getInviteCode());
    }

    // 초대코드 생성 (재생성 시 기존 코드 즉시 무효화)
    // PATCH /api/v1/cats/me/invite-code
    @Transactional
    public InviteCodeResponse generateInviteCode(UUID userId) {
        log.info("[초대코드 생성] userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        String newCode = createRandomCode();
        cat.assignInviteCode(newCode);

        log.info("[초대코드 생성 완료] catId={}", cat.getId());

        return new InviteCodeResponse(newCode);
    }

    private String createRandomCode() {
        StringBuilder sb = new StringBuilder(INVITE_CODE_LENGTH);
        for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
            int idx = ThreadLocalRandom.current().nextInt(INVITE_CODE_CHARS.length());
            sb.append(INVITE_CODE_CHARS.charAt(idx));
        }
        return sb.toString();
    }

    // 초대코드 유효성 검증 + 공동 집사 합류
    // GET /api/v1/cats/invite?code={inviteCode}
    // 유효하지 않은 초대코드면 401 반환, 유효하면 user.catId 저장 후 고양이 정보 반환
    @Transactional
    public InviteCodeValidateResponse validateInviteCode(UUID userId, String inviteCode) {
        log.info("[초대코드 검증] userId={}, inviteCode={}", userId, inviteCode);

        Cat cat = catRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> {
                    log.warn("[초대코드 검증 실패] 존재하지 않는 코드 - inviteCode={}", inviteCode);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "초대코드가 존재하지 않습니다.");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.assignCat(cat);

        log.info("[초대코드 검증 완료] userId={}, catId={}", userId, cat.getId());

        return new InviteCodeValidateResponse(cat.getId(), cat.getName());
    }

    // 고양이 기본 정보 조회
    // GET /api/v1/cats/me
    @Transactional
    public CatInfoResponse getCatInfo(UUID userId) {
        log.info("[고양이 정보 조회] userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        log.info("[고양이 정보 조회 완료] catId={}", cat.getId());

        return new CatInfoResponse(cat.getName(), cat.getBirthDate(), cat.getDiagnosedDate());
    }

    // 고양이 기본 정보 수정
    // PATCH /api/v1/cats/me
    @Transactional
    public MessageResponse updateCatInfo(UUID userId, CatInfoUpdateRequest request) {
        log.info("[고양이 정보 수정] userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cat cat = user.getCat();

        // 더티체킹으로 자동 반영
        cat.updateInfo(request.name(), request.birthDate(), request.diagnosedDate());

        log.info("[고양이 정보 수정 완료] catId={}", cat.getId());

        return new MessageResponse("고양이 정보가 수정되었습니다.");
    }

    // 루틴 스케줄 저장 헬퍼
    // schedules가 빈 배열이면 아무것도 저장하지 않음 (row 미생성)
    // 타입별로 (MEAL / BLOODSUGAR / INSULIN) 각각 호출
    private void saveSchedules(Cat cat, CareScheduleType type, List<CatCreateRequest.ScheduleItem> schedules) {
        for (CatCreateRequest.ScheduleItem item : schedules) {
            // "HH:mm" 문자열 → LocalTime 변환
            LocalTime time = LocalTime.parse(item.time(), TIME_FORMATTER);
            CareSchedule schedule = CareSchedule.create(cat, type, item.sequence(), time);
            careScheduleRepository.save(schedule);
        }
    }
}
