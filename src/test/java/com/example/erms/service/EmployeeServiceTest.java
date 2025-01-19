package com.example.erms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.erms.dao.Employee;
import com.example.erms.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    
    @Mock
    private AuditService auditService;
    
    private EmployeeService employeeService;
    
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepository, auditService);
        
        testEmployee = new Employee();
        testEmployee.setFullName("John Doe");
        testEmployee.setJobTitle("Software Engineer");
        testEmployee.setDepartment("IT");
        testEmployee.setHireDate(LocalDate.now());
        testEmployee.setEmploymentStatus("Active");
        testEmployee.setEmail("john.doe@company.com");
    }

    @Test
    void createEmployee_Success() {
        when(employeeRepository.existsByEmployeeId(anyLong())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        Employee created = employeeService.createEmployee(testEmployee);

        assertNotNull(created);
        assertEquals(testEmployee.getEmployeeId(), created.getEmployeeId());
        verify(auditService).logAction(eq("CREATE"), eq("Employee"), eq(testEmployee.getEmployeeId()), any(), any());
    }

    @Test
    void createEmployee_DuplicateId_ThrowsException() {
        when(employeeRepository.existsByEmployeeId(10000000l)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            employeeService.createEmployee(testEmployee);
        });
    }

    @Test
    void updateEmployee_Success() {
        when(employeeRepository.findByEmployeeId(anyLong())).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        Employee updated = employeeService.updateEmployee(1l, testEmployee);

        assertNotNull(updated);
        assertEquals(testEmployee.getEmployeeId(), updated.getEmployeeId());
        verify(auditService).logAction(eq("UPDATE"), eq("Employee"), eq(testEmployee.getEmployeeId()), any(), any());
    }
    @Test
    void findEmployees_WithStatus() {
        // Arrange
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findByEmploymentStatus("Active")).thenReturn(employees);

        // Act
        List<Employee> found = employeeService.findEmployees(null, "Active");

        // Assert
        assertFalse(found.isEmpty());
        assertEquals("Active", found.get(0).getEmploymentStatus());
    }
}
