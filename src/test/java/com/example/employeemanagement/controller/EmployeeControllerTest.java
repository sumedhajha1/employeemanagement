package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.EmployeeRequestDTO;
import com.example.employeemanagement.dto.EmployeeResponseDTO;
import com.example.employeemanagement.dto.EmployeeStatisticsDTO;
import com.example.employeemanagement.dto.PageResponseDTO;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.exception.GlobalExceptionHandler;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("EmployeeController Integration / WebMvc Tests")
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    private EmployeeRequestDTO validRequestDTO;
    private EmployeeResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        validRequestDTO = new EmployeeRequestDTO(
                "Jane Doe",
                "jane.doe@example.com",
                "Engineering",
                new BigDecimal("75000.00")
        );

        responseDTO = new EmployeeResponseDTO(
                1L,
                "Jane Doe",
                "jane.doe@example.com",
                "Engineering",
                new BigDecimal("75000.00"),
                LocalDateTime.of(2026, 10, 26, 10, 0),
                LocalDateTime.of(2026, 10, 26, 10, 0)
        );
    }

    @Nested
    @DisplayName("POST /api/employees Tests")
    class CreateEmployeeEndpointTests {

        @Test
        @DisplayName("POST /api/employees - Should return 201 Created on valid input")
        void createEmployee_ValidInput_Returns201() throws Exception {
            when(employeeService.createEmployee(any(EmployeeRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(post("/api/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Jane Doe"))
                    .andExpect(jsonPath("$.email").value("jane.doe@example.com"))
                    .andExpect(jsonPath("$.department").value("Engineering"))
                    .andExpect(jsonPath("$.salary").value(75000.00));
        }

        @Test
        @DisplayName("POST /api/employees - Should return 400 Bad Request when validation fails")
        void createEmployee_InvalidInput_Returns400() throws Exception {
            EmployeeRequestDTO invalidRequest = new EmployeeRequestDTO(
                    "",                      // blank name
                    "not-a-valid-email",     // invalid email
                    "",                      // blank department
                    new BigDecimal("-100.00") // negative salary
            );

            mockMvc.perform(post("/api/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.details.name").exists())
                    .andExpect(jsonPath("$.details.email").exists())
                    .andExpect(jsonPath("$.details.department").exists())
                    .andExpect(jsonPath("$.details.salary").exists())
                    .andExpect(jsonPath("$.path").value("/api/employees"));

            verify(employeeService, never()).createEmployee(any(EmployeeRequestDTO.class));
        }

        @Test
        @DisplayName("POST /api/employees - Should return 409 Conflict when email is already registered")
        void createEmployee_DuplicateEmail_Returns409() throws Exception {
            when(employeeService.createEmployee(any(EmployeeRequestDTO.class)))
                    .thenThrow(new DuplicateResourceException("Employee", "email", validRequestDTO.getEmail()));

            mockMvc.perform(post("/api/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.error").value("Conflict"))
                    .andExpect(jsonPath("$.message").value(containsString("already exists with email")))
                    .andExpect(jsonPath("$.path").value("/api/employees"));
        }

        @Test
        @DisplayName("POST /api/employees - Should return 400 Bad Request on malformed JSON")
        void createEmployee_MalformedJson_Returns400() throws Exception {
            String malformedJson = "{ \"name\": \"Jane\", \"salary\": \"invalid-number\" ";

            mockMvc.perform(post("/api/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(malformedJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Malformed JSON request or invalid data format"));
        }

        @Test
        @DisplayName("POST /api/employees - Should return 400 Bad Request when name exceeds 100 characters")
        void createEmployee_NameExceeds100Chars_Returns400() throws Exception {
            String longName = "A".repeat(101);
            EmployeeRequestDTO request = new EmployeeRequestDTO(
                    longName,
                    "valid.email@example.com",
                    "Engineering",
                    new BigDecimal("50000.00")
            );

            mockMvc.perform(post("/api/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.details.name").value("Name must not exceed 100 characters"));
        }

        @Test
        @DisplayName("POST /api/employees - Should return 400 Bad Request when email exceeds 150 characters")
        void createEmployee_EmailExceeds150Chars_Returns400() throws Exception {
            String longEmail = "a".repeat(140) + "@example.com"; // 152 characters total
            EmployeeRequestDTO request = new EmployeeRequestDTO(
                    "Valid Name",
                    longEmail,
                    "Engineering",
                    new BigDecimal("50000.00")
            );

            mockMvc.perform(post("/api/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.details.email").value("Email must not exceed 150 characters"));
        }
    }

    @Nested
    @DisplayName("GET /api/employees Tests")
    class GetAllEmployeesEndpointTests {

        @Test
        @DisplayName("GET /api/employees - Should return 200 OK with paginated response")
        void getAllEmployees_Success_Returns200() throws Exception {
            PageResponseDTO<EmployeeResponseDTO> pagedResponse = new PageResponseDTO<>(
                    List.of(responseDTO), 0, 10, 1L, 1, true, true
            );

            when(employeeService.getAllEmployees(0, 10, "id", "asc", null, null)).thenReturn(pagedResponse);

            mockMvc.perform(get("/api/employees")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sortBy", "id")
                            .param("direction", "asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.pageNumber").value(0))
                    .andExpect(jsonPath("$.pageSize").value(10))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.last").value(true))
                    .andExpect(jsonPath("$.first").value(true));
        }

        @Test
        @DisplayName("GET /api/employees - Should return 400 Bad Request when sort parameter is invalid")
        void getAllEmployees_InvalidSortParam_Returns400() throws Exception {
            when(employeeService.getAllEmployees(0, 10, "invalidField", "asc", null, null))
                    .thenThrow(new IllegalArgumentException("Invalid sort field 'invalidField'. Allowed fields are: id, name, email, department, salary, createdAt, updatedAt"));

            mockMvc.perform(get("/api/employees")
                            .param("sortBy", "invalidField"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value(containsString("Invalid sort field")));
        }
    }

    @Nested
    @DisplayName("Statistics & Department Endpoint Tests")
    class StatisticsAndDepartmentsEndpointTests {

        @Test
        @DisplayName("GET /api/employees/statistics - Should return 200 OK with statistics")
        void getEmployeeStatistics_Returns200() throws Exception {
            EmployeeStatisticsDTO stats = new EmployeeStatisticsDTO(15L, 4L, new BigDecimal("82500.00"), new BigDecimal("130000.00"));
            when(employeeService.getEmployeeStatistics()).thenReturn(stats);

            mockMvc.perform(get("/api/employees/statistics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalEmployees").value(15))
                    .andExpect(jsonPath("$.totalDepartments").value(4))
                    .andExpect(jsonPath("$.averageSalary").value(82500.00))
                    .andExpect(jsonPath("$.highestSalary").value(130000.00));
        }

        @Test
        @DisplayName("GET /api/employees/departments - Should return 200 OK with distinct departments")
        void getDistinctDepartments_Returns200() throws Exception {
            when(employeeService.getDistinctDepartments()).thenReturn(List.of("Engineering", "Finance", "HR"));

            mockMvc.perform(get("/api/employees/departments"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0]").value("Engineering"))
                    .andExpect(jsonPath("$[1]").value("Finance"))
                    .andExpect(jsonPath("$[2]").value("HR"));
        }
    }

    @Nested
    @DisplayName("GET /api/employees/{id} Tests")
    class GetEmployeeByIdEndpointTests {

        @Test
        @DisplayName("GET /api/employees/{id} - Should return 200 OK when employee exists")
        void getEmployeeById_Success_Returns200() throws Exception {
            when(employeeService.getEmployeeById(1L)).thenReturn(responseDTO);

            mockMvc.perform(get("/api/employees/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Jane Doe"))
                    .andExpect(jsonPath("$.email").value("jane.doe@example.com"));
        }

        @Test
        @DisplayName("GET /api/employees/{id} - Should return 404 Not Found when employee is missing")
        void getEmployeeById_NotFound_Returns404() throws Exception {
            when(employeeService.getEmployeeById(999L))
                    .thenThrow(new ResourceNotFoundException("Employee", "id", 999L));

            mockMvc.perform(get("/api/employees/{id}", 999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value("Employee not found with id: '999'"))
                    .andExpect(jsonPath("$.path").value("/api/employees/999"));
        }

        @Test
        @DisplayName("GET /api/employees/{id} - Should return 400 Bad Request on non-numeric ID")
        void getEmployeeById_InvalidIdFormat_Returns400() throws Exception {
            mockMvc.perform(get("/api/employees/{id}", "not-a-number"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value(containsString("Parameter 'id' should be of type Long")));
        }
    }

    @Nested
    @DisplayName("PUT /api/employees/{id} Tests")
    class UpdateEmployeeEndpointTests {

        @Test
        @DisplayName("PUT /api/employees/{id} - Should return 200 OK on valid update")
        void updateEmployee_Success_Returns200() throws Exception {
            when(employeeService.updateEmployee(eq(1L), any(EmployeeRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(put("/api/employees/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Jane Doe"));
        }

        @Test
        @DisplayName("PUT /api/employees/{id} - Should return 404 Not Found when updating non-existent employee")
        void updateEmployee_NotFound_Returns404() throws Exception {
            when(employeeService.updateEmployee(eq(999L), any(EmployeeRequestDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Employee", "id", 999L));

            mockMvc.perform(put("/api/employees/{id}", 999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Employee not found with id: '999'"));
        }

        @Test
        @DisplayName("PUT /api/employees/{id} - Should return 409 Conflict when new email is taken")
        void updateEmployee_DuplicateEmail_Returns409() throws Exception {
            when(employeeService.updateEmployee(eq(1L), any(EmployeeRequestDTO.class)))
                    .thenThrow(new DuplicateResourceException("Employee", "email", validRequestDTO.getEmail()));

            mockMvc.perform(put("/api/employees/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.message").value(containsString("already exists with email")));
        }

        @Test
        @DisplayName("PUT /api/employees/{id} - Should return 400 Bad Request on invalid payload")
        void updateEmployee_InvalidPayload_Returns400() throws Exception {
            EmployeeRequestDTO invalidRequest = new EmployeeRequestDTO("", "bad-email", "", null);

            mockMvc.perform(put("/api/employees/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.details.name").exists())
                    .andExpect(jsonPath("$.details.email").exists());
        }
    }

    @Nested
    @DisplayName("DELETE /api/employees/{id} Tests")
    class DeleteEmployeeEndpointTests {

        @Test
        @DisplayName("DELETE /api/employees/{id} - Should return 204 No Content on successful deletion")
        void deleteEmployee_Success_Returns204() throws Exception {
            doNothing().when(employeeService).deleteEmployee(1L);

            mockMvc.perform(delete("/api/employees/{id}", 1L))
                    .andExpect(status().isNoContent());

            verify(employeeService).deleteEmployee(1L);
        }

        @Test
        @DisplayName("DELETE /api/employees/{id} - Should return 404 Not Found when employee does not exist")
        void deleteEmployee_NotFound_Returns404() throws Exception {
            doThrow(new ResourceNotFoundException("Employee", "id", 999L))
                    .when(employeeService).deleteEmployee(999L);

            mockMvc.perform(delete("/api/employees/{id}", 999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Employee not found with id: '999'"));
        }
    }

    @Nested
    @DisplayName("General Error Handling Tests")
    class GeneralErrorHandlingTests {

        @Test
        @DisplayName("GET /api/unknown-url - Should return 404 Not Found for unknown URLs")
        void unknownUrl_Returns404() throws Exception {
            mockMvc.perform(get("/api/unknown-url"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value("The requested resource was not found"))
                    .andExpect(jsonPath("$.path").value("/api/unknown-url"));
        }

        @Test
        @DisplayName("PATCH /api/employees/{id} - Should return 405 Method Not Allowed on unsupported HTTP method")
        void unsupportedMethod_Returns405() throws Exception {
            mockMvc.perform(patch("/api/employees/{id}", 1L))
                    .andExpect(status().isMethodNotAllowed())
                    .andExpect(jsonPath("$.status").value(405))
                    .andExpect(jsonPath("$.error").value("Method Not Allowed"))
                    .andExpect(jsonPath("$.message").value(containsString("Request method 'PATCH' is not supported")))
                    .andExpect(jsonPath("$.path").value("/api/employees/1"));
        }
    }
}
