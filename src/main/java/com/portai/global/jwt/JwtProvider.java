package com.portai.global.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    // Access Token 만료 시간 (30분)
    @Value("${jwt.expiration_time}")
    private long accessTokenExpirationTime;

    // Refresh Token 만료 시간 (14일)
    @Value("${jwt.refresh_expiration_time}")
    private long refreshTokenExpirationTime;

    // 게스트 토큰 만료 시간 (예: 2시간 = 7200000ms)
    private final long guestTokenExpirationTime = 7200000L;

    private Key key;

    /**
     * 객체 초기화 시 비밀키를 기반으로 암호화 Key 객체를 생성
     */
    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * 사용자 이메일을 기반으로 Access Token을 생성
     * @param email 사용자 이메일
     * @return 생성된 Access Token 문자열
     */
    public String createAccessToken(String email) {
        return createToken(email, "USER", accessTokenExpirationTime);
    }

    /**
     * 사용자 이메일을 기반으로 Refresh Token을 생성
     * @param email 사용자 이메일
     * @return 생성된 Refresh Token 문자열
     */
    public String createRefreshToken(String email) {
        return createToken(email, "REFRESH", refreshTokenExpirationTime);
    }

    /**
     * 게스트(비회원) 임시 토큰 생성
     * @return 생성된 게스트 토큰 문자열
     */
    public String createGuestToken() {
        String guestId = "GUEST_" + UUID.randomUUID();
        return createToken(guestId, "GUEST", guestTokenExpirationTime);
    }

    /**
     * 공통 토큰 생성 로직 (내부에서만 사용하도록 분리)
     */
    private String createToken(String subject, String role, long expirationTime) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(subject) // 토큰의 주체(email 또는 guestId)
                .claim("role", role) // 토큰 종류(USER, REFRESH, GUEST) 구분용 클레임
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(validity) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 암호화 알고리즘과 비밀키 설정
                .compact(); // 토큰 생성
    }

    /**
     * JWT 토큰의 유효성을 검증
     * @param token 프론트엔드에서 전달받은 JWT 토큰
     * @return 유효한 토큰이면 true, 조작되었거나 만료되었으면 false
     */
    public boolean validateToken(String token) {
        try {
            // 토큰을 해독해 보고, 문제가 없으면 true를 반환
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 만료되었거나, 위조되었거나, 형식이 잘못된 경우 모두 에러(Exception)가 발생하여 false 반환
            return false;
        }
    }

    /**
     * 유효한 JWT 토큰에서 사용자 이메일을 추출
     * @param token JWT 토큰
     * @return 토큰에 저장되어 있던 이메일 문자열
     */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // 아까 createToken에서 setSubject()로 넣었던 이메일을 꺼냄
    }

    /**
     * 토큰이 게스트 토큰인지 확인
     * @param token JWT 토큰
     * @return 게스트 토큰이면 true, 아니면 false
     */
    public boolean isGuestToken(String token) {
        try {
            Object role = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role");
            return "GUEST".equals(role);
        } catch (Exception e) {
            return false;
        }
    }
}