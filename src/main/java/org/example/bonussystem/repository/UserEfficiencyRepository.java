package org.example.bonussystem.repository;

import org.example.bonussystem.model.UserEfficiency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserEfficiencyRepository extends JpaRepository<UserEfficiency, Long> {
    List<UserEfficiency> findAllByUserId(Long userId);




}