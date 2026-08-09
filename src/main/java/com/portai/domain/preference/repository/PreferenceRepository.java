package com.portai.domain.preference.repository;

import com.portai.domain.preference.entity.Preference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {

    // 유저 ID로 맞춤화 설정 단건 조회
    Optional<Preference> findByUserId(Long userId);
}