package com.example.erms.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String userId;

	@NotBlank(message = "Full name is required")
	private String fullName;

	@NotBlank(message = "Role is required")
	private UserRoles userRole;
	
	@NotBlank(message = "Job title is required")
	private String jobTitle;

	@NotBlank(message = "Department is required")
	private String department;

	@Email(message = "Invalid email format")
	@NotBlank(message = "Email is required")
	private String email;

	@Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
	private String phone;

}


