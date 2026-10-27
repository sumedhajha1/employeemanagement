package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Employee entity with custom search, filter, and aggregate query methods.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Check if an employee exists with the given email address.
     *
     * @param email Email to verify
     * @return true if an employee exists with this email, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find an employee by their email address.
     *
     * @param email Email to search
     * @return Optional containing the Employee if found
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Check if an employee exists with the given email, excluding a specific employee ID.
     * Useful for checking email uniqueness during update operations.
     *
     * @param email Email to check
     * @param id Employee ID to exclude
     * @return true if another employee already has this email
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Search and filter employees with pagination.
     *
     * @param search Optional search keyword matched against name and email (case-insensitive)
     * @param department Optional department filter (case-insensitive)
     * @param pageable Pagination and sorting parameters
     * @return Page of matching employees
     */
    @Query("SELECT e FROM Employee e WHERE " +
           "(:search IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:department IS NULL OR :department = '' OR LOWER(e.department) = LOWER(:department))")
    Page<Employee> findWithFilter(@Param("search") String search, @Param("department") String department, Pageable pageable);

    /**
     * Retrieve all unique active departments sorted alphabetically.
     *
     * @return List of unique department names
     */
    @Query("SELECT DISTINCT e.department FROM Employee e WHERE e.department IS NOT NULL AND TRIM(e.department) != '' ORDER BY e.department ASC")
    List<String> findDistinctDepartments();

    /**
     * Count unique active departments.
     *
     * @return Count of distinct departments
     */
    @Query("SELECT COUNT(DISTINCT e.department) FROM Employee e WHERE e.department IS NOT NULL AND TRIM(e.department) != ''")
    long countDistinctDepartments();

    /**
     * Calculate the average salary across all registered employees.
     *
     * @return Average salary as Double, or null if no employees exist
     */
    @Query("SELECT AVG(e.salary) FROM Employee e")
    Double getAverageSalary();

    /**
     * Retrieve the highest recorded employee salary.
     *
     * @return Highest salary, or null if no employees exist
     */
    @Query("SELECT MAX(e.salary) FROM Employee e")
    BigDecimal getHighestSalary();
}
