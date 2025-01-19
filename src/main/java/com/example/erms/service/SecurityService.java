//package com.example.erms.service;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import com.example.erms.dao.Employee;
//
//@Service
//public class SecurityService {
//    
//    private final EmployeeService employeeService;
//
//    public SecurityService(EmployeeService employeeService) {
//        this.employeeService = employeeService;
//    }
//
//    public boolean isEmployeeManager(String employeeId) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String currentUsername = auth.getName();
//        
//        Employee employee = employeeService.findByEmployeeId(employeeId);
//        Employee manager = employeeService.findByUsername(currentUsername);
//        
//        return employee != null && manager != null && 
//               employee.getDepartment().equals(manager.getDepartment()) &&
//               manager.getJobTitle().toLowerCase().contains("manager");
//    }
//}