package com.example.employeemanagement.dto;

import com.example.employeemanagement.entity.Employee;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing the employee response returned to clients.
 */
@Schema(description = "Response containing employee details")
public class EmployeeResponseDTO {

    @Schema(description = "Unique employee identifier", example = "1")
    private Long id;

    @Schema(description = "Full name of the employee", example = "Jane Doe")
    private String name;

    @Schema(description = "Corporate email address", example = "jane.doe@example.com")
    private String email;

    @Schema(description = "Department name", example = "Engineering")
    private String department;

    @Schema(description = "Current salary", example = "75000.00")
    private BigDecimal salary;

    @Schema(description = "Timestamp when the record was created", example = "2026-10-26T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the record was last updated", example = "2026-10-26T10:15:30")
    private LocalDateTime updatedAt;

    public EmployeeResponseDTO() {
    }

    public EmployeeResponseDTO(Long id, String name, String email, String department,
                               BigDecimal salary, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.salary = salary;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Factory method to convert an Employee entity to EmployeeResponseDTO.
     */
    public static EmployeeResponseDTO fromEntity(Employee employee) {
        if (employee == null) {
            return null;
        }
        return new EmployeeResponseDTO(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getSalary(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
