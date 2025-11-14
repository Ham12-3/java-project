package com.example.authservice.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerAuditLogRepository extends JpaRepository<CustomerAuditLog, Long> {

    List<CustomerAuditLog> findByProfileIdOrderByCreatedAtDesc(Long profileId);
}
