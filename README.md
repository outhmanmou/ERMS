# Employee Records Management System (ERMS)

A comprehensive Java-based system for managing employee records with both REST API and Swing-based desktop interface.

## Features

- Employee Management (CRUD operations)
- Role-based Access Control (Admin, HR, Manager)
- Search and Filter capabilities
- Audit Logging
- Report Generation
- Swing-based Desktop UI
- REST API with Swagger Documentation

## Technology Stack

- Java 21
- Spring Boot 3.x
- Oracle Database
- Swing (Desktop UI)
- Docker
- Swagger/OpenAPI

## Prerequisites

- Java Development Kit (JDK) 21
- Oracle Database 21c
- Docker
- Maven
- VcXsrv (for Windows GUI support in Docker)

## Local Setup

1. Clone the repository:
```bash
git clone https://github.com/outhmanmou/ERMS.git
cd erms
```

2. Configure Oracle Database:
```sql
-- Connect as SYSDBA
CREATE USER ems_user IDENTIFIED BY ems_password;
GRANT CREATE SESSION TO ems_user;
GRANT CREATE TABLE TO ems_user;
GRANT CREATE SEQUENCE TO ems_user;
GRANT UNLIMITED TABLESPACE TO ems_user;
GRANT CONNECT, RESOURCE TO ems_user;
```

3. Configure application.properties:
```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
spring.datasource.username=ems_user
spring.datasource.password=ems_password
```

4. Build the project:
```bash
mvn clean package
```

5. Run locally:
```bash
mvn spring-boot:run
```

## Docker Setup

1. Install VcXsrv (Windows only):
   - Download from [VcXsrv](https://sourceforge.net/projects/vcxsrv/)
   - Run XLaunch
   - Configure for "Multiple windows"
   - Set Display number: 0
   - Select "Start no client"
   - Check "Disable access control"

2. Build Docker image:
```cmd
docker build -t erms:1.0.0 .
```

3. Create Docker network:
```cmd
docker network create erms-network
```

4. Run container:
```cmd
docker run -d --name erms --network erms-network -p 8080:8080 -e DISPLAY=host.docker.internal:0.0 -e SPRING_DATASOURCE_URL=jdbc:oracle:thin:@oracle_db:1521/XEPDB1 -e SPRING_DATASOURCE_USERNAME=ems_user -e SPRING_DATASOURCE_PASSWORD=ems_password erms:1.0.0
```

## Access Points

- Swing UI: Automatically launches on application start
- REST API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/v3/api-docs

## Default Users

1. Admin User:
   - Username: admin
   - Password: admin123
   - Role: ADMIN

2. HR User:
   - Username: hr
   - Password: hr123
   - Role: HR

3. Manager User:
   - Username: manager
   - Password: manager123
   - Role: MANAGER

e

## Security

Role-based access control is implemented with the following permissions:

- ADMIN: Full access to all endpoints
- HR: Create, read, and update employees
- MANAGER: Read and limited update capabilities

## Testing

Run unit tests:
```bash
mvn test
```

Run integration tests:
```bash
mvn verify
```


Project Link: https://github.com/outhmanmou/ERMS.git
