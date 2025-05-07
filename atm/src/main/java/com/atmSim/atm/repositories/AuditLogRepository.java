package com.atmSim.atm.repositories;

import com.atmSim.atm.entities.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // You can define custom queries here if needed
    List<AuditLog> findByUserId(Long userId);
    List<AuditLog> findByEntity(String entity);
    List<AuditLog> findByAction(String action);
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
