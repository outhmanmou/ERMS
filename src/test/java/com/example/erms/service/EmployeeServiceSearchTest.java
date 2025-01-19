package com.example.erms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import com.example.erms.dao.Employee;
import com.example.erms.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceSearchTest {

    @Mock
    private EmployeeRepository employeeRepository;
    
    @Mock
    private AuditService auditService;
    
    private EmployeeService employeeService;
    
    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepository, auditService);
        
        // Setup test data
        employee1 = new Employee();
        employee1.setEmployeeId(1l);
        employee1.setFullName("John Doe");
        employee1.setDepartment("IT");
        employee1.setEmploymentStatus("Active");
        
        employee2 = new Employee();
        employee2.setEmployeeId(2l);
        employee2.setFullName("Jane Smith");
        employee2.setDepartment("HR");
        employee2.setEmploymentStatus("Active");
    }

    @Test
    void findEmployees_WithDepartment() {
        when(employeeRepository.findAll(any(Specification.class)))
            .thenReturn(Arrays.asList(employee1));

        List<Employee> result = employeeService.findEmployees("IT", null);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("IT", result.get(0).getDepartment());
    }

    @Test
    void searchEmployees_ByName() {
        when(employeeRepository.findAll(any(Specification.class)))
            .thenReturn(Arrays.asList(employee1));

        List<Employee> result = employeeService.searchEmployees("John", "name");
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
    }

    @Test
    void searchEmployees_NoQuery_ReturnsAll() {
        when(employeeRepository.findAll())
            .thenReturn(Arrays.asList(employee1, employee2));

        List<Employee> result = employeeService.searchEmployees("", null);
        
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}