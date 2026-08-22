package com.portai.domain.certificate.repository;

import com.portai.domain.certificate.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findAllByUserId(Long userId);
}