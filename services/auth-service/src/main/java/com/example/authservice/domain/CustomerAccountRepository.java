package com.example.authservice.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerAccountRepository extends JpaRepository<CustomerAccount, Long> {

    Optional<CustomerAccount> findByCustomerId(Long customerId);

    Optional<CustomerAccount> findByAccountNumber(String accountNumber);
}
