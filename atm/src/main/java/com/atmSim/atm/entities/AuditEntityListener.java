package com.atmSim.atm.entities;

import com.atmSim.atm.entities.AuditLog;
import com.atmSim.atm.repositories.AuditLogRepository;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class AuditEntityListener {

    @PrePersist
    @PreUpdate
    @PreRemove
    public void onChange(Object entity) {
        // Save to AuditLog
        AuditLog log = new AuditLog();
        log.setAction(entity.getClass().getSimpleName() + " modified");
        log.setEntity(entity.getClass().getSimpleName());
        log.setUserId(/* fetch from context */ 1L);
        log.setIpAddress(/* fetch from request context */ "127.0.0.1");
        log.setTimestamp(LocalDateTime.now());

        // Inject AuditLogRepository manually or via static accessor
        AuditLogRepository repo = StaticContext.getBean(AuditLogRepository.class);
        repo.save(log);
    }
}
