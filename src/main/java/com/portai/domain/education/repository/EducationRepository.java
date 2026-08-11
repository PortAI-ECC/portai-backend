package com.portai.domain.education.repository;

import com.portai.domain.education.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {

    List<Education> findAllByUserId(Long userId);
}