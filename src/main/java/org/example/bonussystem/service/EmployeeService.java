package org.example.bonussystem.service;

import org.example.bonussystem.model.Employee;
import org.example.bonussystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public void submitEmployeeRequest(String name, Long userId, Long roleId, Long departmentId) {
        Employee employee = new Employee();
        employee.setName(name);
        employee.setStatus("PENDING");
        employee.setUserId(userId);
        employee.setRoleId(roleId);
        employee.setDepartmentId(departmentId);
        employeeRepository.save(employee);
    }

    public List<Employee> getPendingRequests() {
        return employeeRepository.findByStatus("PENDING");
    }

    public void approveEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        employee.setStatus("APPROVED");
        employeeRepository.save(employee);
    }

    public void rejectEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        employee.setStatus("REJECTED");
        employeeRepository.save(employee);
    }
}