package com.example.authservice.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmailIgnoreCase(String email);

    long countByStatus(CustomerStatus status);

    List<Customer> findTop5ByStatusOrderByUpdatedAtDesc(CustomerStatus status);

    Optional<Customer> findByAuthUserId(Long authUserId);
}
