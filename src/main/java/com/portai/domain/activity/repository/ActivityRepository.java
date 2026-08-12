package com.portai.domain.activity.repository;

import com.portai.domain.activity.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findAllByUserId(Long userId);
}
