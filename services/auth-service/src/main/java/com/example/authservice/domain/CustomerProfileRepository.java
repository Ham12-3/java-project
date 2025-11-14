package com.example.authservice.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {

    Optional<CustomerProfile> findByCustomerId(Long customerId);

    List<CustomerProfile> findByCustomerIdIn(Collection<Long> customerIds);

    List<CustomerProfile> findTop5ByRiskScoreGreaterThanEqualOrderByUpdatedAtDesc(BigDecimal minScore);
}
