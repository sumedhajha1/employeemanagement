package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeRequestDTO;
import com.example.employeemanagement.dto.EmployeeResponseDTO;
import com.example.employeemanagement.dto.EmployeeStatisticsDTO;
import com.example.employeemanagement.dto.PageResponseDTO;

import java.util.List;

/**
 * Service interface defining business operations for Employee management.
 */
public interface EmployeeService {

    /**
     * Create a new employee.
     *
     * @param requestDTO Employee creation payload
     * @return Created employee details
     */
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO);

    /**
     * Retrieve a paginated and sorted list of employees.
     *
     * @param page Page index (0-based)
     * @param size Number of items per page
     * @param sortBy Property to sort by
     * @param direction Sort direction ("asc" or "desc")
     * @return Paginated employee details
     */
    default PageResponseDTO<EmployeeResponseDTO> getAllEmployees(int page, int size, String sortBy, String direction) {
        return getAllEmployees(page, size, sortBy, direction, null, null);
    }

    /**
     * Retrieve a paginated, sorted, and filtered list of employees.
     *
     * @param page Page index (0-based)
     * @param size Number of items per page
     * @param sortBy Property to sort by
     * @param direction Sort direction ("asc" or "desc")
     * @param search Optional search keyword (name or email)
     * @param department Optional department filter
     * @return Paginated employee details
     */
    PageResponseDTO<EmployeeResponseDTO> getAllEmployees(int page, int size, String sortBy, String direction, String search, String department);

    /**
     * Retrieve a single employee by their ID.
     *
     * @param id Employee ID
     * @return Employee details
     */
    EmployeeResponseDTO getEmployeeById(Long id);

    /**
     * Update an existing employee.
     *
     * @param id Employee ID to update
     * @param requestDTO Updated employee details
     * @return Updated employee details
     */
    EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO requestDTO);

    /**
     * Delete an employee by their ID.
     *
     * @param id Employee ID to delete
     */
    void deleteEmployee(Long id);

    /**
     * Calculate and return aggregated statistics directly from the database.
     *
     * @return Real-time statistics DTO
     */
    EmployeeStatisticsDTO getEmployeeStatistics();

    /**
     * Retrieve all unique department names for filter options.
     *
     * @return List of department names
     */
    List<String> getDistinctDepartments();
}
