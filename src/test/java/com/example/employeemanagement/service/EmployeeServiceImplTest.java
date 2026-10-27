package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeRequestDTO;
import com.example.employeemanagement.dto.EmployeeResponseDTO;
import com.example.employeemanagement.dto.EmployeeStatisticsDTO;
import com.example.employeemanagement.dto.PageResponseDTO;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService Unit Tests")
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        employee = new Employee(1L, "Alice Walker", "alice.walker@example.com", "Engineering", new BigDecimal("85000.00"));
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());

        requestDTO = new EmployeeRequestDTO("Alice Walker", "alice.walker@example.com", "Engineering", new BigDecimal("85000.00"));
    }

    @Nested
    @DisplayName("Create Employee Tests")
    class CreateEmployeeTests {

        @Test
        @DisplayName("Should create employee successfully when input is valid and email is unique")
        void createEmployee_Success() {
            when(employeeRepository.existsByEmail("alice.walker@example.com")).thenReturn(false);
            when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
                Employee e = invocation.getArgument(0);
                e.setId(1L);
                e.setCreatedAt(LocalDateTime.now());
                e.setUpdatedAt(LocalDateTime.now());
                return e;
            });

            EmployeeResponseDTO response = employeeService.createEmployee(requestDTO);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("Alice Walker");
            assertThat(response.getEmail()).isEqualTo("alice.walker@example.com");
            assertThat(response.getDepartment()).isEqualTo("Engineering");
            assertThat(response.getSalary()).isEqualByComparingTo("85000.00");

            ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
            verify(employeeRepository).save(captor.capture());
            assertThat(captor.getValue().getEmail()).isEqualTo("alice.walker@example.com");
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException when email already exists")
        void createEmployee_DuplicateEmail_ThrowsException() {
            when(employeeRepository.existsByEmail("alice.walker@example.com")).thenReturn(true);

            assertThatThrownBy(() -> employeeService.createEmployee(requestDTO))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("already exists with email: 'alice.walker@example.com'");

            verify(employeeRepository, never()).save(any(Employee.class));
        }
    }

    @Nested
    @DisplayName("Get Employee by ID Tests")
    class GetEmployeeByIdTests {

        @Test
        @DisplayName("Should return employee response when employee is found")
        void getEmployeeById_Success() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

            EmployeeResponseDTO response = employeeService.getEmployeeById(1L);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("Alice Walker");
            assertThat(response.getEmail()).isEqualTo("alice.walker@example.com");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when employee does not exist")
        void getEmployeeById_NotFound_ThrowsException() {
            when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.getEmployeeById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Employee not found with id: '999'");
        }
    }

    @Nested
    @DisplayName("Get All Employees (Pagination, Sorting, Search, Filter) Tests")
    class GetAllEmployeesTests {

        @Test
        @DisplayName("Should return paginated employee list without filters")
        void getAllEmployees_Success() {
            Page<Employee> page = new PageImpl<>(List.of(employee));
            when(employeeRepository.findAll(any(Pageable.class))).thenReturn(page);

            PageResponseDTO<EmployeeResponseDTO> response = employeeService.getAllEmployees(0, 10, "name", "asc");

            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getTotalElements()).isEqualTo(1);
            assertThat(response.getPageNumber()).isEqualTo(0);
            assertThat(response.getPageSize()).isEqualTo(1);
            assertThat(response.getContent().get(0).getName()).isEqualTo("Alice Walker");
        }

        @Test
        @DisplayName("Should use findWithFilter when search or department filter is provided")
        void getAllEmployees_WithSearchAndFilter_Success() {
            Page<Employee> page = new PageImpl<>(List.of(employee));
            when(employeeRepository.findWithFilter(eq("Alice"), eq("Engineering"), any(Pageable.class))).thenReturn(page);

            PageResponseDTO<EmployeeResponseDTO> response = employeeService.getAllEmployees(0, 10, "name", "asc", " Alice ", " Engineering ");

            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            verify(employeeRepository).findWithFilter(eq("Alice"), eq("Engineering"), any(Pageable.class));
            verify(employeeRepository, never()).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when invalid sort field is provided")
        void getAllEmployees_InvalidSortField_ThrowsException() {
            assertThatThrownBy(() -> employeeService.getAllEmployees(0, 10, "invalidField", "asc"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid sort field 'invalidField'");

            verify(employeeRepository, never()).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when invalid direction is provided")
        void getAllEmployees_InvalidDirection_ThrowsException() {
            assertThatThrownBy(() -> employeeService.getAllEmployees(0, 10, "name", "sideways"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid sort direction 'sideways'");

            verify(employeeRepository, never()).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when page number is negative")
        void getAllEmployees_NegativePage_ThrowsException() {
            assertThatThrownBy(() -> employeeService.getAllEmployees(-1, 10, "name", "asc"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Page index must not be less than zero");
        }

        @Test
        @DisplayName("Should cap page size at 100 when requested size is too large")
        void getAllEmployees_CapMaxPageSize() {
            Page<Employee> page = new PageImpl<>(List.of());
            when(employeeRepository.findAll(any(Pageable.class))).thenReturn(page);

            employeeService.getAllEmployees(0, 500, "name", "asc");

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(employeeRepository).findAll(captor.capture());
            assertThat(captor.getValue().getPageSize()).isEqualTo(100);
        }
    }

    @Nested
    @DisplayName("Update Employee Tests")
    class UpdateEmployeeTests {

        @Test
        @DisplayName("Should update employee successfully when updating other fields with same email")
        void updateEmployee_SameEmail_Success() {
            EmployeeRequestDTO updateRequest = new EmployeeRequestDTO("Alice Walker Updated", "alice.walker@example.com", "Management", new BigDecimal("95000.00"));
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            when(employeeRepository.existsByEmailAndIdNot("alice.walker@example.com", 1L)).thenReturn(false);
            when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

            EmployeeResponseDTO response = employeeService.updateEmployee(1L, updateRequest);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Alice Walker Updated");
            assertThat(response.getDepartment()).isEqualTo("Management");
            assertThat(response.getSalary()).isEqualByComparingTo("95000.00");
            verify(employeeRepository).save(employee);
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException when updating email to one that belongs to another employee")
        void updateEmployee_DuplicateEmail_ThrowsException() {
            EmployeeRequestDTO updateRequest = new EmployeeRequestDTO("Alice Walker", "bob.existing@example.com", "Engineering", new BigDecimal("85000.00"));
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            when(employeeRepository.existsByEmailAndIdNot("bob.existing@example.com", 1L)).thenReturn(true);

            assertThatThrownBy(() -> employeeService.updateEmployee(1L, updateRequest))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("already exists with email: 'bob.existing@example.com'");

            verify(employeeRepository, never()).save(any(Employee.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when updating non-existent employee")
        void updateEmployee_NotFound_ThrowsException() {
            when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.updateEmployee(999L, requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Employee not found with id: '999'");

            verify(employeeRepository, never()).save(any(Employee.class));
        }
    }

    @Nested
    @DisplayName("Delete Employee Tests")
    class DeleteEmployeeTests {

        @Test
        @DisplayName("Should delete employee successfully when employee exists")
        void deleteEmployee_Success() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            doNothing().when(employeeRepository).delete(employee);

            employeeService.deleteEmployee(1L);

            verify(employeeRepository).delete(employee);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existent employee")
        void deleteEmployee_NotFound_ThrowsException() {
            when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.deleteEmployee(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Employee not found with id: '999'");

            verify(employeeRepository, never()).delete(any(Employee.class));
        }
    }

    @Nested
    @DisplayName("Statistics & Department Tests")
    class StatisticsAndDepartmentTests {

        @Test
        @DisplayName("Should return valid statistics when employees exist in database")
        void getEmployeeStatistics_WithData_Success() {
            when(employeeRepository.count()).thenReturn(10L);
            when(employeeRepository.countDistinctDepartments()).thenReturn(3L);
            when(employeeRepository.getAverageSalary()).thenReturn(75000.50);
            when(employeeRepository.getHighestSalary()).thenReturn(new BigDecimal("120000.00"));

            EmployeeStatisticsDTO stats = employeeService.getEmployeeStatistics();

            assertThat(stats).isNotNull();
            assertThat(stats.getTotalEmployees()).isEqualTo(10L);
            assertThat(stats.getTotalDepartments()).isEqualTo(3L);
            assertThat(stats.getAverageSalary()).isEqualByComparingTo("75000.50");
            assertThat(stats.getHighestSalary()).isEqualByComparingTo("120000.00");
        }

        @Test
        @DisplayName("Should gracefully handle empty database in statistics calculation")
        void getEmployeeStatistics_EmptyDatabase_GracefulDefaults() {
            when(employeeRepository.count()).thenReturn(0L);
            when(employeeRepository.countDistinctDepartments()).thenReturn(0L);
            when(employeeRepository.getAverageSalary()).thenReturn(null);
            when(employeeRepository.getHighestSalary()).thenReturn(null);

            EmployeeStatisticsDTO stats = employeeService.getEmployeeStatistics();

            assertThat(stats).isNotNull();
            assertThat(stats.getTotalEmployees()).isEqualTo(0L);
            assertThat(stats.getTotalDepartments()).isEqualTo(0L);
            assertThat(stats.getAverageSalary()).isEqualByComparingTo("0.00");
            assertThat(stats.getHighestSalary()).isEqualByComparingTo("0.00");
        }

        @Test
        @DisplayName("Should return distinct sorted list of departments")
        void getDistinctDepartments_Success() {
            List<String> depts = List.of("Engineering", "Finance", "Marketing");
            when(employeeRepository.findDistinctDepartments()).thenReturn(depts);

            List<String> result = employeeService.getDistinctDepartments();

            assertThat(result).containsExactly("Engineering", "Finance", "Marketing");
            verify(employeeRepository).findDistinctDepartments();
        }
    }
}
