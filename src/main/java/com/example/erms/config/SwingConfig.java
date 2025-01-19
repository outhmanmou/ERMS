package com.example.erms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.erms.service.AuditService;
import com.example.erms.service.EmployeeService;
import com.example.erms.ui.MainFrame;

@Configuration
public class SwingConfig {

    @Bean
    public MainFrame mainFrame(EmployeeService employeeService, AuditService auditService) {
        return new MainFrame(employeeService, auditService);
    }
}