package com.portai.domain.user.controller;

import com.portai.domain.user.dto.*;
import com.portai.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    // Servic 호출
    private final UserService userService;

    // 회원가입 API (POST 방식으로 요청을 보내면 실행됨)
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {

        userService.signup(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다!");
    }

    /**
     * 사용자 로그인 API
     * @param request 사용자 이메일, 비밀번호
     * @return 로그인 성공 시 AuthResponse (유저 식별자, 이름, 토큰) 반환
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {

        // 1. Service의 로그인 로직을 호출하고, 그 결과(AuthResponse)를 받음
        AuthResponse response = userService.login(request);

        // 2. 프론트엔드에 HTTP 상태 200(OK)과 함께 로그인 성공 데이터를 반환
        return ResponseEntity.ok(response);
    }

    /**
     * Access Token 갱신 API
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody TokenRefreshRequest request) {
        AuthResponse response = userService.refresh(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 로그아웃 API
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestBody LogoutRequest request) {

        // 1. 서비스의 로그아웃 로직(DB에서 토큰 삭제) 실행
        userService.logout(request);

        // 2. 명세서에 맞춰 성공 메시지 반환
        return ResponseEntity.ok(new MessageResponse("로그아웃 되었습니다."));
    }
}