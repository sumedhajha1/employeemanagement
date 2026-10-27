# Employee Management System (Full-Stack Spring Boot + React Dashboard)

A complete, production-ready, beginner-friendly **Employee Management System** built with **Java 17+**, **Spring Boot 3.3.5**, **Spring Data JPA**, **Hibernate**, **PostgreSQL**, and a modern **React + Tailwind CSS** admin dashboard.

Designed with clean layered architecture, Jakarta Bean Validation, real-time database-driven statistics, server-side pagination, multi-field sorting, instant search & department filtering, JUnit 5 & Mockito test coverage, interactive Swagger/OpenAPI documentation, Docker support, and a complete 14-request Postman collection.

---

## Table of Contents
1. [Key Features](#key-features)
2. [Technology Stack](#technology-stack)
3. [Prerequisites](#prerequisites)
4. [Project Structure](#project-structure)
5. [Database Configuration & Setup](#database-configuration--setup)
6. [Step-by-Step Run Instructions (Windows PowerShell & macOS/Linux)](#step-by-step-run-instructions)
7. [Running Tests](#running-tests)
8. [API Endpoints & Documentation](#api-endpoints--documentation)
9. [Dashboard Features & Real-Time Statistics](#dashboard-features--real-time-statistics)
10. [Error Handling & Validation Rules](#error-handling--validation-rules)
11. [Postman Collection Guide](#postman-collection-guide)
12. [Docker Deployment](#docker-deployment)
13. [Troubleshooting Guide](#troubleshooting-guide)
14. [Interview Explanation Cheat Sheet](#interview-explanation-cheat-sheet)

---

## Key Features

### Backend (Spring Boot 3 & PostgreSQL)
- **Layered Architecture:** Controller -> Service -> Repository -> Entity, with constructor-based dependency injection.
- **DTO Encapsulation:** JPA Entities are never directly exposed. Dedicated Request, Response, and Paginated DTOs protect internal schemas.
- **Jakarta Bean Validation:** Validates required fields, corporate email formatting, non-blank strings, and non-negative salary values.
- **Safe Uniqueness Handling:**
  - Prevents registering duplicate email addresses on creation (`existsByEmail`).
  - Allows an employee to keep their existing email during updates, while preventing collision with other employees' emails (`existsByEmailAndIdNot`).
- **Real-Time Aggregated Statistics API (`GET /api/employees/statistics`):** Calculates Total Employees, Distinct Active Departments, Average Salary, and Highest Salary directly using database aggregation (`COUNT`, `AVG`, `MAX`), gracefully handling empty database states.
- **Server-Side Pagination, Sorting, Search & Filtering:**
  - `GET /api/employees?page=0&size=10&sortBy=name&direction=asc&search=john&department=Engineering`
  - Validates sort fields, caps page size at 100, and filters dynamically with JPQL queries.
- **Global Exception Handling:** `@RestControllerAdvice` returns uniform JSON envelopes with proper HTTP status codes (200, 201, 204, 400, 404, 409, 500) without exposing internal stack traces.
- **Interactive Swagger/OpenAPI Docs:** Explore and execute live endpoints through Swagger UI.

### Frontend (React 18 + Tailwind CSS Dashboard)
- **Executive Metric Cards:** Total Employees, Total Departments, Average Salary, and Highest Salary updating in real time after create, edit, or delete actions.
- **Configurable Currency:** Instantly switch between USD (`$`), INR (`₹`), EUR (`€`), and GBP (`£`) across the entire dashboard.
- **Clean Employee Directory Table:** Displays Avatar initials, Name, Corporate Email, Department badge, Formatted Salary, and Creation Date.
- **Instant Search & Filter:** Search by employee name or email; filter by department using dynamic values loaded from `/api/employees/departments`.
- **Full Column Sorting:** Click any column header (ID, Name, Department, Salary, Date) to toggle ascending/descending order.
- **Interactive Modals:**
  - **Add Employee:** With client-side and server-side validation error displays.
  - **Edit Employee:** Pre-populates existing data.
  - **Employee Profile View:** Displays complete record including exact audit timestamps.
  - **Delete Confirmation:** Warning dialog with employee name to prevent accidental deletion.
- **Toast Notifications:** Automatic feedback for successful mutations and detailed API errors.
- **Responsive Layout:** Works smoothly on desktop, tablet, and mobile screens.

---

## Technology Stack

| Layer | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Backend Framework** | Spring Boot | 3.3.5 | REST API, DI, auto-configuration |
| **Language** | Java | 17+ (Tested on Java 17, 21, 26) | Strongly-typed backend |
| **Persistence** | Spring Data JPA / Hibernate | 6.x | ORM & query generation |
| **Primary Database** | PostgreSQL | 14+ / 16 | Relational data persistence |
| **Testing DB** | H2 (In-Memory) | 2.x | Zero-dependency fast tests |
| **Validation** | Jakarta Bean Validation | 3.0+ | Hibernate Validator annotations |
| **API Docs** | Springdoc OpenAPI / Swagger UI | 2.6.0 | Interactive API documentation |
| **Testing** | JUnit 5, Mockito, Spring WebMvcTest | Latest | Service and Controller test suite |
| **Frontend** | React, Vite, Tailwind CSS | 18.3+ / 5.4+ / 3.4+ | Modern reactive single-page app |
| **Icons** | Lucide React | 0.454+ | Clean modern icons |
| **Containers** | Docker & Docker Compose | 3.8 | Multi-container setup |

---

## Prerequisites

1. **Java JDK 17 or later**:
   ```powershell
   java -version
   ```
2. **Node.js (v18 or later)**:
   ```powershell
   node -v
   npm.cmd -v
   ```
3. **PostgreSQL** (or Docker):
   ```powershell
   psql --version
   ```
4. *Maven is optional* because the Maven Wrapper (`.\mvnw.cmd` on Windows or `./mvnw` on Unix) is already included in the project.

---

## Project Structure

```
Employeemanagement/
├── .mvn/wrapper/                  # Maven Wrapper binaries & properties
├── frontend/                      # React 18 + Tailwind CSS Dashboard
│   ├── src/
│   │   ├── api/
│   │   │   └── employeeService.js # API client for Spring Boot endpoints
│   │   ├── components/
│   │   │   ├── DeleteConfirmModal.jsx # Delete confirmation dialog
│   │   │   ├── EmployeeDetailsModal.jsx # Full profile view modal
│   │   │   ├── EmployeeModal.jsx      # Add / Edit form with validation
│   │   │   ├── EmployeeTable.jsx      # Table with sorting & filters
│   │   │   ├── Header.jsx             # Top bar, currency switcher, refresh
│   │   │   ├── Pagination.jsx         # Pagination & page size selector
│   │   │   ├── Sidebar.jsx            # Modern navigation sidebar
│   │   │   ├── StatsOverview.jsx      # 4 Metric overview cards
│   │   │   └── Toast.jsx              # Floating notification banners
│   │   ├── App.jsx                    # Root state coordinator
│   │   ├── index.css                  # Tailwind styles
│   │   └── main.jsx                   # React entry point
│   ├── package.json                   # Frontend dependencies
│   ├── tailwind.config.js             # Tailwind CSS config
│   └── vite.config.js                 # Vite dev server & proxy settings
├── src/
│   ├── main/
│   │   ├── java/com/example/employeemanagement/
│   │   │   ├── config/
│   │   │   │   ├── OpenApiConfig.java # Swagger/OpenAPI setup
│   │   │   │   └── WebConfig.java     # Global CORS configuration
│   │   │   ├── controller/
│   │   │   │   └── EmployeeController.java # REST Endpoints
│   │   │   ├── dto/
│   │   │   │   ├── EmployeeRequestDTO.java  # Validated incoming payload
│   │   │   │   ├── EmployeeResponseDTO.java # Entity-isolated response
│   │   │   │   ├── EmployeeStatisticsDTO.java # Aggregated metrics DTO
│   │   │   │   └── PageResponseDTO.java     # Paginated response wrapper
│   │   │   ├── entity/
│   │   │   │   └── Employee.java            # JPA Database Entity
│   │   │   ├── exception/
│   │   │   │   ├── DuplicateResourceException.java # 409 Conflict
│   │   │   │   ├── ErrorResponse.java             # Standard error envelope
│   │   │   │   ├── GlobalExceptionHandler.java     # @RestControllerAdvice
│   │   │   │   └── ResourceNotFoundException.java  # 404 Not Found
│   │   │   ├── repository/
│   │   │   │   └── EmployeeRepository.java        # Spring Data JPA queries
│   │   │   ├── service/
│   │   │   │   ├── EmployeeService.java           # Service interface
│   │   │   │   └── impl/EmployeeServiceImpl.java  # Business logic & metrics
│   │   │   └── EmployeeManagementApplication.java # Spring Boot main entry
│   │   └── resources/
│   │       ├── application.properties             # Database & server settings
│   │       └── schema.sql                         # PostgreSQL schema & seed data
│   └── test/
│       ├── java/com/example/employeemanagement/
│       │   ├── controller/
│       │   │   └── EmployeeControllerTest.java     # WebMvc endpoint tests
│       │   └── service/
│       │       └── EmployeeServiceImplTest.java    # Service unit tests
│       └── resources/
│           └── application-test.properties        # In-memory H2 test config
├── .env.example                   # Environment variable template
├── .gitignore                     # Git ignore rules (includes frontend dist & node_modules)
├── Dockerfile                     # Multi-stage Docker build
├── docker-compose.yml             # Spring Boot + PostgreSQL compose setup
├── Employee_Management_API.postman_collection.json # 14 Postman requests
├── mvnw / mvnw.cmd                # Maven wrapper scripts
├── pom.xml                        # Maven dependencies
└── README.md                      # Project documentation
```

---

## Database Configuration & Setup

The application uses a PostgreSQL database named `employeedb`. The database
username is normally `postgres`, but the password is the one selected during
PostgreSQL installation. It is not necessarily `postgres`.

### PostgreSQL connection details

| Setting | Value |
| :--- | :--- |
| Server | `localhost` |
| Database | `employeedb` |
| Port | `5432` |
| Username | `postgres` |
| Password | Your PostgreSQL installation password |

### Step 1: Open SQL Shell (psql)

Open **SQL Shell (psql)** from the Windows Start menu and enter:

```text
Server: localhost
Database: postgres
Port: 5432
Username: postgres
Password: Your PostgreSQL installation password
```

The password is not displayed while typing; this is normal.

### Step 2: Create the application database

After connecting successfully, run:

```sql
CREATE DATABASE employeedb;
```

If PostgreSQL reports that the database already exists, continue to the next
step.

### Step 3: Open the project folder

Open **File Explorer**, navigate to the project folder, and open PowerShell
there. The project is expected at:

```text
C:\Users\Sumedha\Desktop\Employeemanagement
```

You can also navigate to it directly:

```powershell
cd "$env:USERPROFILE\Desktop\Employeemanagement"
```

### Step 4: Configure the PostgreSQL password

Run these commands in the same PowerShell window. Replace
`your-actual-password` with the password used during PostgreSQL installation:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/employeedb"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your-actual-password"
```

Do not use the literal text `your-actual-password`; replace it with the real
PostgreSQL password. These environment variables apply only to the current
PowerShell window.

The application uses these defaults:
```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/employeedb}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
spring.jpa.hibernate.ddl-auto=update
```

- Hibernate creates or updates the `employees` table when the application
  starts.
- For production, use `ddl-auto=validate` with a migration tool such as Flyway
  or Liquibase.

A standalone SQL script is also provided at `src/main/resources/schema.sql`.

---

## Step-by-Step Run Instructions (Windows)

### Step 5: Build and start the backend

In the project PowerShell window, run:

```powershell
.\mvnw.cmd clean package
```

Wait for the build to complete successfully, then start Spring Boot:

```powershell
.\mvnw.cmd spring-boot:run
```

Keep this PowerShell window open while using the application. The backend runs
at **`http://localhost:8080`**.

If you open **`http://localhost:8080`** directly and see an **Internal Server
Error** or a Spring Boot error page, this is expected because the application
does not define a page for the root (`/`) URL. It does not mean that the
backend has failed.

Swagger API documentation is available at:
**`http://localhost:8080/swagger-ui.html`**

If Java is not installed or the command is not recognized, check it with:

```powershell
java -version
```

Install and configure Java JDK 17 or later if no version is shown.

You can alternatively run the already-built JAR:

```powershell
java -jar target\employeemanagement-1.0.0.jar
```

### Step 6: Start the frontend dashboard

Open a **second PowerShell window**, navigate to the project, and run:

```powershell
cd "$env:USERPROFILE\Desktop\Employeemanagement\frontend"
npm.cmd install
npm.cmd run dev
```

The dashboard runs at **`http://localhost:5173`**. Open that URL in a browser.

### Step 7: Open the application

Open these URLs in a browser:

- **Backend:** [http://localhost:8080](http://localhost:8080)
- **Swagger API documentation:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Employee API:** [http://localhost:8080/api/employees](http://localhost:8080/api/employees)
- **Employee dashboard:** [http://localhost:5173](http://localhost:5173)

Use the **Swagger API documentation** or the **Employee API** link to verify
the backend. Use the **Employee dashboard** link to use the application.

### macOS/Linux alternative

On macOS or Linux, use the equivalent commands:

```bash
export DB_URL="jdbc:postgresql://localhost:5432/employeedb"
export DB_USERNAME="postgres"
export DB_PASSWORD="your-actual-password"
./mvnw clean package
./mvnw spring-boot:run
```

In a second terminal:

```bash
cd frontend
npm install
npm run dev
```

---

## Running Tests

All automated unit and controller tests use an in-memory H2 database (`src/test/resources/application-test.properties`). **No external PostgreSQL database is needed to run the test suite.**

```powershell
# Windows
.\mvnw.cmd test

# macOS / Linux
./mvnw test
```

### Test Suite Results:
- **35 automated tests executed** with **0 failures and 0 errors**.
  - `EmployeeServiceImplTest`: 17 unit tests verifying creation, duplicate email rejection, safe email updating, not-found exceptions, deletion, sorting validation, max page size capping, statistics calculations, and distinct departments.
  - `EmployeeControllerTest`: 18 WebMvc integration tests verifying HTTP status codes (201, 200, 204, 400, 404, 409), input validation details, statistics endpoint, and distinct departments endpoint.

### Running Frontend Build Verification:
```powershell
cd frontend
npm.cmd run build
```
Builds the production bundle into `frontend/dist/` in seconds.

---

## API Endpoints & Documentation

Base URL: `http://localhost:8080/api/employees`

| HTTP Method | Endpoint | Description | Status Code |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/employees` | Create a new employee | `201 Created` |
| `GET` | `/api/employees` | Retrieve paginated, sorted, and filtered employees | `200 OK` |
| `GET` | `/api/employees/statistics` | Retrieve real-time database aggregated metrics | `200 OK` |
| `GET` | `/api/employees/departments` | Retrieve list of all unique departments | `200 OK` |
| `GET` | `/api/employees/{id}` | Retrieve employee by ID | `200 OK` / `404 Not Found` |
| `PUT` | `/api/employees/{id}` | Update existing employee (safe email check) | `200 OK` / `404 Not Found` / `409 Conflict` |
| `DELETE` | `/api/employees/{id}` | Delete employee by ID | `204 No Content` / `404 Not Found` |

### Interactive Swagger UI:
After starting the backend, open Swagger UI in your browser:

👉 **[Open Swagger UI](http://localhost:8080/swagger-ui.html)**

Direct URL: `http://localhost:8080/swagger-ui.html`

You can use the Swagger page to view the API documentation and try the endpoints
directly. The backend must be running on port `8080`. The raw OpenAPI JSON
schema is available at `http://localhost:8080/v3/api-docs`.

---

### Request & Response Examples

#### 1. Create Employee (`POST /api/employees`)
**Request:**
```json
{
  "name": "Jane Doe",
  "email": "jane.doe@example.com",
  "department": "Engineering",
  "salary": 85000.00
}
```
**Response (`201 Created`):**
```json
{
  "id": 1,
  "name": "Jane Doe",
  "email": "jane.doe@example.com",
  "department": "Engineering",
  "salary": 85000.00,
  "createdAt": "2026-10-27T00:10:00",
  "updatedAt": "2026-10-27T00:10:00"
}
```

#### 2. Get Statistics (`GET /api/employees/statistics`)
**Response (`200 OK`):**
```json
{
  "totalEmployees": 15,
  "totalDepartments": 4,
  "averageSalary": 78500.50,
  "highestSalary": 125000.00
}
```

#### 3. Paginated, Sorted & Filtered Listing (`GET /api/employees?page=0&size=10&sortBy=name&direction=asc&search=john&department=Engineering`)
**Response (`200 OK`):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@example.com",
      "department": "Engineering",
      "salary": 85000.00,
      "createdAt": "2026-10-27T00:10:00",
      "updatedAt": "2026-10-27T00:10:00"
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true
}
```

---

## Error Handling & Validation Rules

All error responses strictly follow a uniform JSON structure:

```json
{
  "timestamp": "2026-10-27T00:10:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": {
    "email": "Must be a valid email address",
    "name": "Name is required and cannot be blank",
    "salary": "Salary must be a non-negative decimal value"
  },
  "path": "/api/employees"
}
```

- **`400 Bad Request`**: Triggered on blank fields, invalid email syntax, negative salary, malformed JSON payloads, or exceeding length/precision constraints:
  - `name`: max 100 characters (`@Size(max = 100)`)
  - `email`: max 150 characters (`@Size(max = 150)`)
  - `department`: max 100 characters (`@Size(max = 100)`)
  - `salary`: max 10 integer digits and 2 decimal places (`@Digits(integer = 10, fraction = 2)`)
- **`404 Not Found`**: Triggered when accessing, updating, or deleting a non-existent employee ID, or when requesting an unknown URL/resource (`NoResourceFoundException`).
- **`405 Method Not Allowed`**: Triggered when invoking an unsupported HTTP verb on an endpoint (e.g. `PATCH /api/employees/{id}` via `HttpRequestMethodNotSupportedException`).
- **`409 Conflict`**: Triggered when attempting to assign an email already registered to another employee.
- **`500 Internal Server Error`**: Catches unexpected runtime errors safely without exposing server stack traces.

---

## Postman Collection Guide

A complete 14-request collection is located in the project root:  
[`Employee_Management_API.postman_collection.json`](file:///c:/Users/Sumedha/Desktop/Employeemanagement/Employee_Management_API.postman_collection.json)

### How to Import:
1. Open Postman.
2. Click **Import** (top left).
3. Select `Employee_Management_API.postman_collection.json`.
4. Run requests in sequence to test creation, validation errors, duplicate email conflict, sorting, filtering, statistics, and deletion.

---

## Docker Deployment

To run both PostgreSQL 16 and the Spring Boot application in containers:

```powershell
# Build and start services
docker-compose up --build -d

# Check running containers
docker-compose ps

# View application logs
docker-compose logs -f app

# Stop containers
docker-compose down
```

---

## Troubleshooting Guide

1. **`Connection to localhost:5432 refused`**:
   - PostgreSQL service is not running. Start PostgreSQL from Windows Services or run Docker Desktop.
   - Verify PostgreSQL is listening on port 5432: `Test-NetConnection -ComputerName localhost -Port 5432`.
2. **`database "employeedb" does not exist`**:
   - Create the database: `CREATE DATABASE employeedb;` in PostgreSQL.
3. **`npm.ps1 cannot be loaded because running scripts is disabled`**:
   - Use `npm.cmd run dev` or `npm.cmd run build` instead of `npm`.
4. **Port 8080 already in use**:
   - Check what process is occupying port 8080: `Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess`.
   - Or change the port in `application.properties`: `server.port=8081`.

---

## Interview Explanation Cheat Sheet

| Question | What to explain |
| :--- | :--- |
| **Why use DTOs?** | To avoid exposing JPA entity internal structures to clients, allow targeted validation, and decouple the database schema from the external API contract. |
| **How does email uniqueness work on updates?** | We use Spring Data JPA's `existsByEmailAndIdNot(email, id)`. If another employee owns the email (`id != currentId`), it throws a `409 Conflict`. If the employee keeps their own email, it updates successfully. |
| **How are statistics calculated efficiently?** | The database calculates `COUNT()`, `COUNT(DISTINCT department)`, `AVG(salary)`, and `MAX(salary)` directly via SQL aggregate functions. No heavy data is loaded into application memory. |
| **Why constructor injection over `@Autowired` on fields?** | Constructor injection guarantees that required dependencies are initialized (making objects immutable with `final`), avoids reflection, and makes unit testing with Mockito effortless. |
| **How does CORS work in this setup?** | `WebConfig.java` implements `WebMvcConfigurer` to allow cross-origin requests from `http://localhost:5173` to `http://localhost:8080`. Vite also includes a development proxy configuration. |
