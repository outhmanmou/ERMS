package com.example.erms.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erms.dao.AuditLog;
import com.example.erms.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class AuditService {

	private final AuditLogRepository auditLogRepository;
	private final ObjectMapper objectMapper;

	public AuditService(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
		this.auditLogRepository = auditLogRepository;
		this.objectMapper = objectMapper;
	}

	public void logAction(String action, String entityType, long entityId, String user, String details) {
		AuditLog auditLog = new AuditLog();
		auditLog.setAction(action);
		auditLog.setEntityType(entityType);
		auditLog.setEntityId(entityId);
		auditLog.setModifiedBy(user != null ? user : getCurrentUser());
		auditLog.setModifiedAt(LocalDateTime.now());
		auditLog.setChanges(details);

		auditLogRepository.save(auditLog);
	}

	public void logChanges(String action, String entityType, long entityId, Object oldValue, Object newValue) {
		try {
			// Convert objects to maps for comparison
			Map<String, Object> oldMap = objectMapper.convertValue(oldValue, Map.class);
			Map<String, Object> newMap = objectMapper.convertValue(newValue, Map.class);

			// Build changes description
			StringBuilder changes = new StringBuilder();
			for (String key : newMap.keySet()) {
				Object oldVal = oldMap.get(key);
				Object newVal = newMap.get(key);

				if (!newVal.equals(oldVal)) {
					changes.append(String.format("%s: %s → %s; ", key, oldVal, newVal));
				}
			}

			// Log the changes
			logAction(action, entityType, entityId, null, changes.toString());
		} catch (Exception e) {
			// Fallback to simple logging if comparison fails
			logAction(action, entityType, entityId, null, "Modified entity");
		}
	}

	public List<AuditLog> getAuditLogs(String entityType, Long entityId, String action) {
		if (entityType != null && entityId != null && action != null) {
			return auditLogRepository.findByEntityTypeAndEntityIdAndAction(entityType, entityId, action);
		} else if (entityType != null && entityId != null) {
			return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
		} else if (entityType != null) {
			return auditLogRepository.findByEntityType(entityType);
		} else if (action != null) {
			return auditLogRepository.findByAction(action);
		}
		return auditLogRepository.findAll();
	}

	public Page<AuditLog> getAuditLogs(Pageable pageable) {
		return auditLogRepository.findAll(pageable);
	}

	public List<AuditLog> getEntityHistory(String entityType, long entityId) {
		return auditLogRepository.findByEntityTypeAndEntityIdOrderByModifiedAtDesc(entityType, entityId);
	}

	private String getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null ? auth.getName() : "SYSTEM";
	}
	 public List<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
	        return auditLogRepository.findByModifiedAtBetweenOrderByModifiedAtDesc(startDate, endDate);
	    }
}