package org.example.bonussystem.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    private LocalDateTime requestDate;




    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "user_id")
    private Long userId;


    // Getter and Setter
    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }
    // Геттеры и сеттеры
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Дополнительные методы для совместимости
    public Long getRoleId() {
        return role != null ? role.getId() : null;
    }

    public void setRoleId(Long roleId) {
        if (this.role == null) {
            this.role = new Role();
        }
        this.role.setId(roleId);

    }

    public Long getDepartmentId() {
        return department != null ? department.getId() : null;
    }

    public void setDepartmentId(Long departmentId) {
        if (this.department == null) {
            this.department = new Department();
        }
        this.department.setId(departmentId);
    }


}