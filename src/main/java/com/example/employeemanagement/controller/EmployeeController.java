package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.EmployeeRequestDTO;
import com.example.employeemanagement.dto.EmployeeResponseDTO;
import com.example.employeemanagement.dto.EmployeeStatisticsDTO;
import com.example.employeemanagement.dto.PageResponseDTO;
import com.example.employeemanagement.exception.ErrorResponse;
import com.example.employeemanagement.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing Employee management endpoints and analytical reporting.
 */
@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH})
@Tag(name = "Employee Management", description = "Endpoints for creating, retrieving, updating, deleting, searching, and analyzing employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    // Constructor-based dependency injection
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Create a new employee.
     */
    @PostMapping
    @Operation(summary = "Create an employee", description = "Creates a new employee record. Requires unique corporate email address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee successfully created",
                    content = @Content(schema = @Schema(implementation = EmployeeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed for input fields",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Employee with provided email already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@Valid @RequestBody EmployeeRequestDTO requestDTO) {
        EmployeeResponseDTO created = employeeService.createEmployee(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Retrieve all employees with pagination, sorting, search, and department filtering.
     */
    @GetMapping
    @Operation(summary = "Retrieve all employees", description = "Returns a paginated list of employees with sorting, search, and department filter support.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved employee list",
                    content = @Content(schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or sorting parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponseDTO<EmployeeResponseDTO>> getAllEmployees(
            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size (max 100)", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Property to sort by (id, name, email, department, salary, createdAt, updatedAt)", example = "name")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Sort direction ('asc' or 'desc')", example = "asc")
            @RequestParam(defaultValue = "asc") String direction,

            @Parameter(description = "Optional search query matched against employee name or email", example = "john")
            @RequestParam(required = false) String search,

            @Parameter(description = "Optional filter by department", example = "Engineering")
            @RequestParam(required = false) String department) {

        PageResponseDTO<EmployeeResponseDTO> employees = employeeService.getAllEmployees(page, size, sortBy, direction, search, department);
        return ResponseEntity.ok(employees);
    }

    /**
     * Retrieve aggregated statistics calculated from database records.
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get employee statistics", description = "Calculates total employees, distinct departments, average salary, and highest salary directly from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated statistics",
                    content = @Content(schema = @Schema(implementation = EmployeeStatisticsDTO.class)))
    })
    public ResponseEntity<EmployeeStatisticsDTO> getEmployeeStatistics() {
        EmployeeStatisticsDTO statistics = employeeService.getEmployeeStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Retrieve a list of distinct departments.
     */
    @GetMapping("/departments")
    @Operation(summary = "Get distinct departments", description = "Returns a sorted list of all unique department names present in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved department list")
    })
    public ResponseEntity<List<String>> getDistinctDepartments() {
        List<String> departments = employeeService.getDistinctDepartments();
        return ResponseEntity.ok(departments);
    }

    /**
     * Retrieve an employee by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Retrieve an employee by ID", description = "Returns employee details for the given ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee found and returned",
                    content = @Content(schema = @Schema(implementation = EmployeeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID parameter format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found with provided ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(
            @Parameter(description = "ID of the employee to retrieve", example = "1")
            @PathVariable Long id) {

        EmployeeResponseDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    /**
     * Update an existing employee.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an employee", description = "Updates details of an existing employee. Will not allow duplicate emails belonging to other employees.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee successfully updated",
                    content = @Content(schema = @Schema(implementation = EmployeeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed for input fields",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found with provided ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email is already taken by another employee",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @Parameter(description = "ID of the employee to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDTO requestDTO) {

        EmployeeResponseDTO updated = employeeService.updateEmployee(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete an employee by ID.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee", description = "Deletes an employee record by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employee successfully deleted, no content returned"),
            @ApiResponse(responseCode = "400", description = "Invalid ID parameter format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found with provided ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteEmployee(
            @Parameter(description = "ID of the employee to delete", example = "1")
            @PathVariable Long id) {

        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
