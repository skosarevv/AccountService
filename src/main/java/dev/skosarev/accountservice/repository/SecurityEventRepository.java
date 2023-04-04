package dev.skosarev.accountservice.repository;

import dev.skosarev.accountservice.model.SecurityEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityEventRepository extends JpaRepository<SecurityEvent, Long> {
}
