package com.example.erms.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.erms.dao.AuditLog;
import com.example.erms.service.AuditService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/audit")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Audit Logs", description = "APIs for accessing audit logs")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }
    
    @Operation(summary = "Get audit logs", description = "Retrieve audit logs with filtering options")
    @GetMapping
    public ResponseEntity<List<AuditLog>> getAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) Long entityId,
            @RequestParam(required = false) String action) {
        return ResponseEntity.ok(auditService.getAuditLogs(entityType, entityId, action));
    }

    @Operation(
        summary = "Get recent audit logs",
        description = "Retrieves the most recent audit logs"
    )
    @GetMapping("/recent")
    public ResponseEntity<List<AuditLog>> getRecentLogs(
            @Parameter(description = "Number of logs to retrieve")
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(auditService.getAuditLogs(null,null,null));
    }

    @Operation(
        summary = "Get audit logs by date range",
        description = "Retrieves audit logs within a specified date range"
    )
    @GetMapping("/date-range")
    public ResponseEntity<List<AuditLog>> getLogsByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(auditService.getAuditLogsByDateRange(startDate, endDate));
    }
}
