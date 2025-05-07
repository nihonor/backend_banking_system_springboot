package com.atmSim.atm.controller;

import com.atmSim.atm.entities.AuditLog;
import com.atmSim.atm.repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public List<AuditLog> getAll() {
        return auditLogRepository.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<AuditLog> getByUserId(@PathVariable Long userId) {
        return auditLogRepository.findByUserId(userId);
    }

    @GetMapping("/entity/{entity}")
    public List<AuditLog> getByEntity(@PathVariable String entity) {
        return auditLogRepository.findByEntity(entity);
    }

    @GetMapping("/action/{action}")
    public List<AuditLog> getByAction(@PathVariable String action) {
        return auditLogRepository.findByAction(action);
    }

    @GetMapping("/range")
    public List<AuditLog> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return auditLogRepository.findByTimestampBetween(start, end);
    }
}
