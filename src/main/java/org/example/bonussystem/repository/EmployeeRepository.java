package org.example.bonussystem.repository;

import org.example.bonussystem.model.Department;
import org.example.bonussystem.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByUserId(Long userId);
    Employee findEmployeeByUserId(Long userId);
    List<Employee> findByStatus(String status);
    List<Employee> findAllByDepartment(Department department);
}
