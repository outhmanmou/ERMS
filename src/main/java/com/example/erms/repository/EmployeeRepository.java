package com.example.erms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.erms.dao.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    boolean existsByEmployeeId(long employeeId);
    Optional<Employee> findByEmployeeId(long employeeId);
    Integer deleteByEmployeeId(long employeeId);
    List<Employee> findByEmploymentStatus(String employmentStatus);

    
}