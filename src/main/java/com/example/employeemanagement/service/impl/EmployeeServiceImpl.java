package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.dto.EmployeeRequestDTO;
import com.example.employeemanagement.dto.EmployeeResponseDTO;
import com.example.employeemanagement.dto.EmployeeStatisticsDTO;
import com.example.employeemanagement.dto.PageResponseDTO;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Service implementation for managing employee records and analytics.
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private static final int MAX_PAGE_SIZE = 100;

    private static final Set<String> ALLOWED_SORT_FIELDS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("id", "name", "email", "department", "salary", "createdAt", "updatedAt"))
    );

    private final EmployeeRepository employeeRepository;

    // Constructor-based dependency injection
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO) {
        logger.info("Attempting to create employee with email: {}", requestDTO.getEmail());

        String email = requestDTO.getEmail().trim().toLowerCase();

        // Check if an employee with this email already exists
        if (employeeRepository.existsByEmail(email)) {
            logger.warn("Employee creation failed: email '{}' already in use", email);
            throw new DuplicateResourceException("Employee", "email", email);
        }

        Employee employee = new Employee();
        employee.setName(requestDTO.getName().trim());
        employee.setEmail(email);
        employee.setDepartment(requestDTO.getDepartment().trim());
        employee.setSalary(requestDTO.getSalary());

        Employee savedEmployee = employeeRepository.save(employee);
        logger.info("Successfully created employee with id: {}", savedEmployee.getId());

        return EmployeeResponseDTO.fromEntity(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<EmployeeResponseDTO> getAllEmployees(int page, int size, String sortBy, String direction, String search, String department) {
        logger.debug("Fetching employees: page={}, size={}, sortBy={}, direction={}, search={}, department={}",
                page, size, sortBy, direction, search, department);

        // Validate page parameters
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero");
        }
        if (size > MAX_PAGE_SIZE) {
            logger.info("Requested page size {} exceeds maximum allowed of {}. Limiting to {}", size, MAX_PAGE_SIZE, MAX_PAGE_SIZE);
            size = MAX_PAGE_SIZE;
        }

        // Validate sort field
        if (sortBy == null || !ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException(String.format(
                    "Invalid sort field '%s'. Allowed fields are: %s",
                    sortBy, String.join(", ", ALLOWED_SORT_FIELDS)
            ));
        }

        // Validate and create sort direction
        Sort.Direction sortDirection;
        if ("desc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.DESC;
        } else if ("asc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.ASC;
        } else {
            throw new IllegalArgumentException(String.format(
                    "Invalid sort direction '%s'. Allowed directions are: 'asc' or 'desc'", direction
            ));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        String sanitizedSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        String sanitizedDepartment = (department != null && !department.trim().isEmpty()) ? department.trim() : null;

        Page<Employee> employeePage;
        if (sanitizedSearch != null || sanitizedDepartment != null) {
            employeePage = employeeRepository.findWithFilter(sanitizedSearch, sanitizedDepartment, pageable);
        } else {
            employeePage = employeeRepository.findAll(pageable);
        }

        Page<EmployeeResponseDTO> dtoPage = employeePage.map(EmployeeResponseDTO::fromEntity);
        return PageResponseDTO.of(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) {
        logger.debug("Fetching employee by id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Employee with id {} was not found", id);
                    return new ResourceNotFoundException("Employee", "id", id);
                });

        return EmployeeResponseDTO.fromEntity(employee);
    }

    @Override
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO requestDTO) {
        logger.info("Attempting to update employee with id: {}", id);

        // Verify employee exists
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Employee update failed: id {} not found", id);
                    return new ResourceNotFoundException("Employee", "id", id);
                });

        String newEmail = requestDTO.getEmail().trim().toLowerCase();

        // Check if another employee already has this email (excluding current employee)
        if (employeeRepository.existsByEmailAndIdNot(newEmail, id)) {
            logger.warn("Employee update failed: email '{}' already in use by another employee", newEmail);
            throw new DuplicateResourceException("Employee", "email", newEmail);
        }

        // Update fields
        existingEmployee.setName(requestDTO.getName().trim());
        existingEmployee.setEmail(newEmail);
        existingEmployee.setDepartment(requestDTO.getDepartment().trim());
        existingEmployee.setSalary(requestDTO.getSalary());

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        logger.info("Successfully updated employee with id: {}", updatedEmployee.getId());

        return EmployeeResponseDTO.fromEntity(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {
        logger.info("Attempting to delete employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Employee deletion failed: id {} not found", id);
                    return new ResourceNotFoundException("Employee", "id", id);
                });

        employeeRepository.delete(employee);
        logger.info("Successfully deleted employee with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeStatisticsDTO getEmployeeStatistics() {
        logger.debug("Calculating employee statistics from database");

        long totalEmployees = employeeRepository.count();
        long totalDepartments = employeeRepository.countDistinctDepartments();
        Double avgSalaryDouble = employeeRepository.getAverageSalary();
        BigDecimal highestSalary = employeeRepository.getHighestSalary();

        // Gracefully handle empty database state
        BigDecimal safeAvgSalary;
        if (totalEmployees == 0 || avgSalaryDouble == null) {
            safeAvgSalary = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        } else {
            safeAvgSalary = BigDecimal.valueOf(avgSalaryDouble).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal safeHighestSalary = (totalEmployees == 0 || highestSalary == null)
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : highestSalary.setScale(2, RoundingMode.HALF_UP);

        return new EmployeeStatisticsDTO(totalEmployees, totalDepartments, safeAvgSalary, safeHighestSalary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctDepartments() {
        logger.debug("Fetching all unique departments");
        return employeeRepository.findDistinctDepartments();
    }
}
