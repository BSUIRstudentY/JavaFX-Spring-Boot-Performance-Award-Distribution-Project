package org.example.bonussystem.repository;

import org.example.bonussystem.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}