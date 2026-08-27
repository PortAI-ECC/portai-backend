package com.portai.global.config;

import com.portai.global.resolver.AuthUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthUserArgumentResolver authUserArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 우리가 만든 리졸버를 스프링에 등록
        resolvers.add(authUserArgumentResolver);
    }

    // 업로드된 파일을 외부(프론트엔드 img 태그 등)에서 접근할 수 있도록 경로 열어주기
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

    /**
     * 프론트엔드(portai-front) 연동용 CORS 설정.
     * SecurityConfig의 .cors(Customizer.withDefaults())가 이 빈을 그대로 사용함.
     * TODO: 프론트 배포 도메인이 확정되면 allowedOriginPatterns에 실제 주소로 교체/추가
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",   // 로컬 프론트 (Next.js/CRA 기본 포트)
                "http://localhost:5173",   // 로컬 프론트 (Vite 기본 포트)
                "https://*.vercel.app",     // Vercel 배포/프리뷰 도메인
                "https://ecc-portai.netlify.app" // Netlify 프론트 배포 도메인
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // 쿠키/Authorization 헤더 포함 요청 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}