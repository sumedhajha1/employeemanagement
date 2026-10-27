package com.example.employeemanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Data Transfer Object for aggregated Employee statistics.
 */
@Schema(description = "Real-time aggregated statistics calculated from database records")
public class EmployeeStatisticsDTO {

    @Schema(description = "Total count of registered employees", example = "42")
    private long totalEmployees;

    @Schema(description = "Count of distinct active departments", example = "5")
    private long totalDepartments;

    @Schema(description = "Average salary across all employees", example = "78500.50")
    private BigDecimal averageSalary;

    @Schema(description = "Highest recorded employee salary", example = "125000.00")
    private BigDecimal highestSalary;

    public EmployeeStatisticsDTO() {
    }

    public EmployeeStatisticsDTO(long totalEmployees, long totalDepartments, BigDecimal averageSalary, BigDecimal highestSalary) {
        this.totalEmployees = totalEmployees;
        this.totalDepartments = totalDepartments;
        this.averageSalary = averageSalary;
        this.highestSalary = highestSalary;
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public long getTotalDepartments() {
        return totalDepartments;
    }

    public void setTotalDepartments(long totalDepartments) {
        this.totalDepartments = totalDepartments;
    }

    public BigDecimal getAverageSalary() {
        return averageSalary;
    }

    public void setAverageSalary(BigDecimal averageSalary) {
        this.averageSalary = averageSalary;
    }

    public BigDecimal getHighestSalary() {
        return highestSalary;
    }

    public void setHighestSalary(BigDecimal highestSalary) {
        this.highestSalary = highestSalary;
    }
}
