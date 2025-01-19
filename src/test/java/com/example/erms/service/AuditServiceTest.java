package com.example.erms.service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.erms.dao.AuditLog;
import com.example.erms.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

	@Mock
	private AuditLogRepository auditLogRepository;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private SecurityContext securityContext;

	@Mock
	private Authentication authentication;

	private AuditService auditService;

	@BeforeEach
	void setUp() {
		auditService = new AuditService(auditLogRepository, objectMapper);
		SecurityContextHolder.setContext(securityContext);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn("testUser");
	}

	@Test
	void logAction_Success() {
		auditService.logAction("CREATE", "Employee", 1l, null, "Created new employee");
		verify(auditLogRepository, times(1)).save(any(AuditLog.class));
	}

	@Test
	void logChanges_Success() {
		TestEntity oldEntity = new TestEntity("old name");
		TestEntity newEntity = new TestEntity("new name");

		when(objectMapper.convertValue(any(), eq(Map.class)))
		.thenReturn(Map.of("name", "old name"))
		.thenReturn(Map.of("name", "new name"));

		auditService.logChanges("UPDATE", "TestEntity", 1l, oldEntity, newEntity);
		verify(auditLogRepository, times(1)).save(any(AuditLog.class));
	}

	private static class TestEntity {
		private String name;

		public TestEntity(String name) {
			this.name = name;
		}
	}
}