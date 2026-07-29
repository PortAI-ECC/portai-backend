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
     */
    @Transactional
    public void signup(SignupRequest request) {

        // 1. 평문 비밀번호 암호화 (BCrypt)
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 2. DTO 데이터를 바탕으로 User 엔티티 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword) // 암호화된 비밀번호 할당
                .name(request.getName())
                .phone(request.getPhone())
                .desiredJob(request.getDesiredJob())
                .build();

        // 3. DB에 사용자 정보 저장
        userRepository.save(user);
    }

    /**
     * 사용자 로그인 로직 (토큰 2개 발급 및 리프레시 토큰 DB 저장)
     * @param request 프론트엔드에서 전달받은 로그인 정보 (이메일, 비밀번호)
     * @return AuthResponse (명세서에 맞춘 응답)
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {

        // 1. 이메일로 DB에서 사용자 조회 (없으면 USER_NOT_FOUND)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 비밀번호 일치 여부 검증 (틀리면 INVALID_PASSWORD)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 3. JwtProvider를 사용해 Access / Refresh 토큰 2개 발급
        String accessToken = jwtProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getEmail());

        // 4. DB에 리프레시 토큰 저장 (이미 있다면 새 토큰으로 업데이트, 없다면 새로 생성)
        RefreshToken tokenEntity = refreshTokenRepository.findByUserEmail(user.getEmail())
                .orElse(new RefreshToken(user.getEmail(), refreshToken));

        tokenEntity.updateToken(refreshToken);
        refreshTokenRepository.save(tokenEntity);

        // 5. 프론트엔드 명세서 구조에 맞게 내부 객체 생성
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        // 6. 로그인 성공 정보 및 토큰을 명세서와 동일한 폼으로 반환 (expiresIn: 30분 = 1800초)
        return new AuthResponse(
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
    public AuthResponse refresh(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        // 1. 넘어온 리프레시 토큰이 유효한지 검사 (위조/만료 시 INVALID_TOKEN)
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 2. 토큰에서 유저 이메일 추출
        String email = jwtProvider.getEmailFromToken(refreshToken);

        // 3. DB에 저장된 리프레시 토큰 꺼내오기 (없으면 SESSION_NOT_FOUND)
        RefreshToken savedToken = refreshTokenRepository.findByUserEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        // 4. 프론트엔드가 보낸 토큰과 DB에 저장된 토큰이 똑같은지 비교 (다르면 TOKEN_MISMATCH)
        if (!savedToken.getToken().equals(refreshToken)) {
            throw new CustomException(ErrorCode.TOKEN_MISMATCH);
        }

        // 5. 모든 검사를 통과했으므로, 새로운 Access Token 발급
        String newAccessToken = jwtProvider.createAccessToken(email);

        // 6. 유저 정보 조회 (없으면 USER_NOT_FOUND)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        // 7. 새 Access Token을 담아서 반환 (Refresh Token은 기존 것 그대로 유지)
        return new AuthResponse(
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

        // 1. 넘어온 토큰이 유효한지 검사 (위조/만료 시 INVALID_TOKEN)
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 2. 토큰에서 유저 이메일 추출
        String email = jwtProvider.getEmailFromToken(refreshToken);

        // 3. 해당 유저의 리프레시 토큰을 DB에서 완전히 삭제 (폐기)
        refreshTokenRepository.deleteByUserEmail(email);
    }
}