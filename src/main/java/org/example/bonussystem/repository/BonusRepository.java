package org.example.bonussystem.repository;

import org.example.bonussystem.model.Bonus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BonusRepository extends JpaRepository<Bonus, Long> {
    List<Bonus> findAllByEmployeeId(Long employeeId);
}