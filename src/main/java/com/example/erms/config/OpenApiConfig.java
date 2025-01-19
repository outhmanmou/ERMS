package com.example.erms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI employeeManagementOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Employee Records Management System API")
						.description("REST API for managing employee records, including CRUD operations and audit logging")
						.version("1.0.0")
						.contact(new Contact()
								.name("Your Name")
								.email("your.email@example.com"))
						.license(new License()
								.name("Apache 2.0")
								.url("http://www.apache.org/licenses/LICENSE-2.0.html")));
	}
}