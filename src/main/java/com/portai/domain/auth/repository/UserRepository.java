package com.portai.domain.auth.repository;

import com.portai.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 1. 회원가입 로직용: 이미 존재하는 이메일인지 검사 (true/false 반환)
    boolean existsByEmail(String email);

    // 2. 로그인 로직용: 이메일을 던져서 비밀번호 등 전체 회원 정보를 가져옴
    Optional<User> findByEmail(String email);

}