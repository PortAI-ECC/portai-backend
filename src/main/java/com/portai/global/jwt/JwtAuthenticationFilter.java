package com.portai.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. 프론트엔드가 보낸 요청(Header)에서 암호화된 토큰(출입증)만 가져옴
        String token = resolveToken(request);

        // 2. 토큰이 존재하고, 유효한(조작되지 않은) 토큰인지 검사
        if (token != null && jwtProvider.validateToken(token)) {

            String email = jwtProvider.getEmailFromToken(token);

            // 3. 증명서 만들고 서버(SecurityContext)에 등록
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 4. 검사 종료 후 다음 단계 넘어감
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP Header에서 'Bearer ' 글자를 떼어내고 순수한 토큰 문자열만 추출하는 도우미 메서드
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 진짜 토큰 부분만 잘라서 반환
        }
        return null;
    }
}