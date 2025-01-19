package com.example.erms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.erms.dao.Employee;
import com.example.erms.repository.EmployeeRepository;

import jakarta.persistence.criteria.Predicate;

@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final AuditService auditService;

    public EmployeeService(EmployeeRepository employeeRepository, AuditService auditService) {
        this.employeeRepository = employeeRepository;
        this.auditService = auditService;
    }

    public Employee createEmployee(Employee employee) {
        Employee savedEmployee = employeeRepository.save(employee);
        auditService.logAction("CREATE", "Employee", employee.getEmployeeId(), "System", "Created new employee");
        return savedEmployee;
    }

    public Employee updateEmployee(long employeeId, Employee employee) {
        Employee existingEmployee = employeeRepository.findByEmployeeId(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
            
        // Update fields
        existingEmployee.setFullName(employee.getFullName());
        existingEmployee.setJobTitle(employee.getJobTitle());
        existingEmployee.setDepartment(employee.getDepartment());
        existingEmployee.setEmploymentStatus(employee.getEmploymentStatus());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setPhone(employee.getPhone());
        existingEmployee.setAddress(employee.getAddress());

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        auditService.logAction("UPDATE", "Employee", employeeId, "System", "Updated employee details");
        return updatedEmployee;
    }
    
    public List<Employee> findEmployees(String department, String status) {
        Specification<Employee> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Add department filter
            if (StringUtils.hasText(department)) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("department")), 
                    department.toLowerCase()
                ));
            }
            
            // Add status filter
            if (StringUtils.hasText(status)) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("employmentStatus")), 
                    status.toLowerCase()
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return employeeRepository.findAll(spec);
    }

    public List<Employee> searchEmployees(String query, String field) {
        if (!StringUtils.hasText(query)) {
            return employeeRepository.findAll();
        }
        
        Specification<Employee> spec = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            String searchTerm = "%" + query.toLowerCase() + "%";
            
            if (StringUtils.hasText(field)) {
                // Search in specific field
                switch (field.toLowerCase()) {
                    case "name":
                        predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("fullName")), 
                            searchTerm
                        ));
                        break;
                    case "id":
                        predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("employeeId")), 
                            searchTerm
                        ));
                        break;
                    case "title":
                        predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("jobTitle")), 
                            searchTerm
                        ));
                        break;
                    case "department":
                        predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("department")), 
                            searchTerm
                        ));
                        break;
                    case "email":
                        predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("email")), 
                            searchTerm
                        ));
                        break;
                    default:
                        // If field is not recognized, search in all searchable fields
                        predicates.add(getDefaultSearchPredicate(root, criteriaBuilder, searchTerm));
                }
            } else {
                // If no specific field is specified, search in all searchable fields
                predicates.add(getDefaultSearchPredicate(root, criteriaBuilder, searchTerm));
            }
            
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
        
        return employeeRepository.findAll(spec);
    }

    private Predicate getDefaultSearchPredicate(jakarta.persistence.criteria.Root<Employee> root,
                                              jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
                                              String searchTerm) {
        return criteriaBuilder.or(
            criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), searchTerm),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("employeeId")), searchTerm),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("jobTitle")), searchTerm),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("department")), searchTerm),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchTerm)
        );
    }

	public Employee findByEmployeeId(Long employeeId) {
		// TODO Auto-generated method stub
		if(employeeRepository.findByEmployeeId(employeeId).isPresent()) {
			return employeeRepository.findByEmployeeId(employeeId).get();
		}
		return null;
	}

	public void deleteEmployee(Long employeeId) {
		// TODO Auto-generated method stub
		employeeRepository.deleteByEmployeeId(employeeId);
	}
	
	public Employee updateOrSave( Employee employee) {
		// TODO Auto-generated method stub
		return employeeRepository.save(employee);
	}
}
