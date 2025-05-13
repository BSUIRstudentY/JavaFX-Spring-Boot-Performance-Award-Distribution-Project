package org.example.bonussystem.repository;

import org.example.bonussystem.model.PerformanceIndicator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceIndicatorRepository extends JpaRepository<PerformanceIndicator, Long> {
    List<PerformanceIndicator> findAllByEmployeeId(Long employeeId);
}