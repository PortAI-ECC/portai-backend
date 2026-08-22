package com.portai.domain.user.service;

import com.portai.domain.user.dto.*;
import com.portai.domain.user.entity.RefreshToken;
import com.portai.domain.user.entity.User;
import com.portai.domain.user.repository.RefreshTokenRepository;
import com.portai.domain.user.repository.UserRepository;
import com.portai.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.portai.global.exception.CustomException;
import com.portai.global.exception.ErrorCode;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 사용자 회원가입 로직
     * @param request 프론트엔드에서 전달받은 회원가입 정보
     * @return SignupResponse (회원가입 결과 및 유저 정보)
     */
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // 1. 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 2. 평문 비밀번호 암호화 (BCrypt)
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. DTO 데이터를 바탕으로 User 엔티티 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .phone(request.getPhone())
                .build();

        User savedUser = userRepository.save(user);

        // 4. SignupResponse DTO에 담아서 반환
        return new SignupResponse(
                savedUser.getId(),
                "회원가입이 완료되었습니다!",
                savedUser.getName(),
                savedUser.getEmail()
        );
    }

    /**
     * 사용자 로그인 로직 (토큰 2개 발급 및 리프레시 토큰 DB 저장)
     */
    @Transactional
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getEmail());

        RefreshToken tokenEntity = refreshTokenRepository.findByUserEmail(user.getEmail())
                .orElse(new RefreshToken(user.getEmail(), refreshToken));

        tokenEntity.updateToken(refreshToken);
        refreshTokenRepository.save(tokenEntity);

        UserResponse.UserInfo userInfo = new UserResponse.UserInfo(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        return new UserResponse(
                "로그인 성공",
                accessToken,
                refreshToken,
                1800L,
                userInfo
        );
    }

    /**
     * Access Token 갱신 로직
     */
    @Transactional
    public UserResponse refresh(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        // 방어 코드: 토큰 앞에 "Bearer "가 붙어 있다면 떼어냄
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String email = jwtProvider.getEmailFromToken(refreshToken);

        RefreshToken savedToken = refreshTokenRepository.findByUserEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        if (!savedToken.getToken().equals(refreshToken)) {
            throw new CustomException(ErrorCode.TOKEN_MISMATCH);
        }

        String newAccessToken = jwtProvider.createAccessToken(email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserResponse.UserInfo userInfo = new UserResponse.UserInfo(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        return new UserResponse(
                "토큰 갱신 성공",
                newAccessToken,
                refreshToken,
                1800L,
                userInfo
        );
    }

    /**
     * 사용자 로그아웃 로직 (DB에서 Refresh Token 삭제)
     */
    @Transactional
    public void logout(LogoutRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String email = jwtProvider.getEmailFromToken(refreshToken);
        refreshTokenRepository.deleteByUserEmail(email);
    }


    /**
     * 게스트(비회원) 임시 세션 생성 로직 (최종 수정본)
     */
    @Transactional
    public GuestResponse createGuestSession() {
        // 1. 겹치지 않는 임시 식별자(UUID) 생성 (앞 8자리)
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        // 2. 임시 유저 엔티티 생성
        User guestUser = User.builder()
                .name("게스트_" + uuid)
                .email("guest_" + uuid + "@portai.temp") // 고유한 임시 이메일
                .password(UUID.randomUUID().toString())
                .build();

        // 3. 임시 유저를 DB에 실제로 저장
        userRepository.save(guestUser);

        // 방금 만든 임시 이메일을 넘겨주어 "USER" 권한을 가진 일반 토큰을 발급받음
        String accessToken = jwtProvider.createAccessToken(guestUser.getEmail());

        return new GuestResponse(
                "게스트 토큰 발급 및 임시 유저 생성 성공",
                accessToken,
                7200L // 프론트엔드 반환용 만료 시간
        );
    }
}