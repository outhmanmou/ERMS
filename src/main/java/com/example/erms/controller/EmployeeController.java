package com.example.erms.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.erms.dao.Employee;
import com.example.erms.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee Management", description = "APIs for managing employee records")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    @Operation(summary = "Create new employee", description = "Create a new employee record")
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.createEmployee(employee));
    }

    @PutMapping("/{employeeId}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or @securityService.isEmployeeManager(#employeeId)")
    @Operation(summary = "Update employee", description = "Update an existing employee record")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable long employeeId,
            @Valid @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.updateEmployee(employeeId, employee));
    }

    @GetMapping
    @Operation(summary = "Get all employees", description = "Retrieve all employees with filtering options")
    public ResponseEntity<List<Employee>> getAllEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(employeeService.findEmployees(department, status));
    }

    @GetMapping("/search")
    @Operation(summary = "Search employees", description = "Search employees by various criteria")
    public ResponseEntity<List<Employee>> searchEmployees(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String field) {
        return ResponseEntity.ok(employeeService.searchEmployees(query, field));
    }
}
