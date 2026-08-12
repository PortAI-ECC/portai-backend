package com.portai.global.config;

import com.portai.global.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 비밀번호 암호화 Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // 보안 규칙 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // 프론트엔드 연결을 위해 꺼둠
                // CorsConfigurationSource 빈(WebConfig)을 그대로 사용
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
// CORS 프리플라이트 요청은 인증 없이 통과
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 회원가입, 로그인, 게스트 토큰 발급 주소는 누구나 접근 가능하도록 개방
                        .requestMatchers("/api/auth/signup", "/api/auth/login", "/api/auth/guest").permitAll()
                        // 스웨거 UI와 API 문서 접근 허용
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "swagger-ui.html").permitAll()
                        // 나머지 주소는 로그인해야만 접근 가능
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}