package com.example.authservice.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerDocumentRepository extends JpaRepository<CustomerDocument, Long> {

    List<CustomerDocument> findByProfileIdOrderByUploadedAtDesc(Long profileId);

    Optional<CustomerDocument> findByIdAndProfileId(Long id, Long profileId);
}
