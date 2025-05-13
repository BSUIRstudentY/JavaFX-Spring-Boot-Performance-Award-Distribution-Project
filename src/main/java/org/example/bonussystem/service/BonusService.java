package org.example.bonussystem.service;

import org.example.bonussystem.model.*;
import org.example.bonussystem.repository.*;
import org.hibernate.type.ListType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BonusService {
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;
    private final PerformanceIndicatorRepository piRepository;
    private final BonusRepository bonusRepository;

    @Autowired
    public BonusService(
            DepartmentRepository departmentRepository,
            RoleRepository roleRepository,
            EmployeeRepository employeeRepository,
            PerformanceIndicatorRepository piRepository,
            BonusRepository bonusRepository
    ) {
        this.departmentRepository = departmentRepository;
        this.roleRepository = roleRepository;
        this.employeeRepository = employeeRepository;
        this.piRepository = piRepository;
        this.bonusRepository = bonusRepository;
    }

    // Методы для отделов
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }

    public void saveDepartment(Department department) {
        departmentRepository.save(department);
    }

    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    // Методы для ролей
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public void saveRole(Role role) {
        roleRepository.save(role);
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    // Методы для сотрудников
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public void saveEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    // Методы для показателей
    public List<PerformanceIndicator> getAllPerformanceIndicators() {
        return piRepository.findAll();
    }

    public PerformanceIndicator getPerformanceIndicatorById(Long id) {
        return piRepository.findById(id).orElse(null);
    }

    public void savePerformanceIndicator(PerformanceIndicator pi) {
        piRepository.save(pi);
    }

    public void deletePerformanceIndicator(Long id) {
        piRepository.deleteById(id);
    }

    // Методы для премий
    public List<Bonus> getAllBonuses() {
        return bonusRepository.findAll();
    }

    public Bonus getBonusById(Long id) {
        return bonusRepository.findById(id).orElse(null);
    }

    public void saveBonus(Bonus bonus) {
        bonusRepository.save(bonus);
    }


    public List<Bonus> findAllByEmployeeId(Long id)
    {
        return bonusRepository.findAllByEmployeeId(id);
    }


    public void deleteBonus(Long id) {
        bonusRepository.deleteById(id);
    }

    public void calculateBonus(PerformanceIndicator pi) {
        Employee employee = pi.getEmployee();
        if (employee == null || employee.getRole() == null) return;

        Double baseBonusRate = employee.getRole().getBaseBonusRate();
        if (baseBonusRate == null) {
            baseBonusRate = 0.0; // Значение по умолчанию, если baseBonusRate не указано
        }

        double bonusAmount = pi.getRevenue() * baseBonusRate * (pi.getKpi() / 100.0);

        Bonus bonus = new Bonus();
        bonus.setEmployee(employee);
        bonus.setAmount(bonusAmount);
        bonus.setYear(pi.getYear());
        bonusRepository.save(bonus);
    }
}