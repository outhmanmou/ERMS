package com.example.erms.dao;


import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "employees")
public class Employee {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long employeeId;

// @NotBlank(message = "Employee ID is required")
// @Column(unique = true)
// private String employeeId= new StringBuilder().append(id).toString();

 @NotBlank(message = "Full name is required")
 private String fullName;

 @NotBlank(message = "Job title is required")
 private String jobTitle;

 @NotBlank(message = "Department is required")
 private String department;

 @NotNull(message = "Hire date is required")
 private LocalDate hireDate;

 @NotBlank(message = "Employment status is required")
 private String employmentStatus;

 @Email(message = "Invalid email format")
 @NotBlank(message = "Email is required")
 private String email;

 private String phone;

 @NotBlank(message = "Address is required")
 private String address;
}

