package com.portai.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 프로젝트 전역 에러 코드.
 * 도메인별로 구역을 나눠서 각자 담당 부분에만 추가하면 충돌이 적습니다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ===== 공통 =====
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    FILE_STORAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장 중 오류가 발생했습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),

    // ===== 인증/세션 (윤지) =====
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "세션을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "가입되지 않은 이메일이거나 사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 토큰입니다."),
    TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "토큰 정보가 일치하지 않습니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 가입된 이메일입니다."),

    // ===== 기술 스택 (윤지) =====
    DUPLICATE_TECH_STACK(HttpStatus.BAD_REQUEST, "이미 등록된 기술입니다."),
    TECH_STACK_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 기술 스택을 찾을 수 없거나 권한이 없습니다."),

    // ===== 공모전 (윤지) =====
    CONTEST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 공모전 이력을 찾을 수 없습니다."),

    // ===== 인턴/경력 (윤지) =====
    CAREER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 경력을 찾을 수 없습니다."),

    // ===== 맞춤화 설정 (윤지) =====
    PREFERENCE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저의 맞춤화 설정을 찾을 수 없습니다."),

    // ===== 프로젝트 (지호) =====
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다."),
    PROJECT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 프로젝트에 대한 권한이 없습니다."),
    PROJECT_ATTACHMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 발표자료를 찾을 수 없습니다."),

    // ===== 생성/LLM (지호) =====
    GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "생성에 실패했습니다."),
    LLM_CLIENT_ERROR(HttpStatus.BAD_GATEWAY, "LLM 호출 중 오류가 발생했습니다."),
    GENERATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 생성 요청을 찾을 수 없거나 권한이 없습니다."),
    GENERATION_RESULT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유형의 생성 결과를 찾을 수 없습니다."),

    // ===== 외부 연동 (지호) =====
    GITHUB_REPO_NOT_FOUND(HttpStatus.NOT_FOUND, "GitHub 저장소를 찾을 수 없습니다."),
    GITHUB_API_ERROR(HttpStatus.BAD_GATEWAY, "GitHub API 호출에 실패했습니다."),
    INTEGRATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 연동 정보를 찾을 수 없거나 권한이 없습니다."),
    DUPLICATE_INTEGRATION(HttpStatus.BAD_REQUEST, "이미 등록된 플랫폼입니다."),

    // ===== 채용공고 분석 (지호) =====
    JOB_POSTING_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 채용공고를 찾을 수 없거나 권한이 없습니다."),

    // ===== 활동/포트폴리오/자격증 (가현) =====
    ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND, "활동을 찾을 수 없습니다."),
    ACTIVITY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 활동에 대한 권한이 없습니다."),
    CERTIFICATE_NOT_FOUND(HttpStatus.NOT_FOUND, "자격증을 찾을 수 없습니다."),
    CERTIFICATE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 자격증에 대한 권한이 없습니다."),
    EDUCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "학력을 찾을 수 없습니다."),
    EDUCATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 학력에 대한 권한이 없습니다."),
    PORTFOLIO_NOT_FOUND(HttpStatus.NOT_FOUND, "포트폴리오를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
