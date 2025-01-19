package com.example.erms.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.erms.dao.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByEntityType(String entityType);
    List<AuditLog> findByEntityId(Long entityId);
    List<AuditLog> findByAction(String action);
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<AuditLog> findByEntityTypeAndEntityIdAndAction(String entityType, Long entityId, String action);
    List<AuditLog> findByEntityTypeAndEntityIdOrderByModifiedAtDesc(String entityType, Long entityId);

    
    List<AuditLog> findByModifiedAtBetweenOrderByModifiedAtDesc(
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    
    List<AuditLog> findByActionOrderByModifiedAtDesc(String action);
}
