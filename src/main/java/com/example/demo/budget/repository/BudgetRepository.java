package com.example.demo.budget.repository;

import com.example.demo.budget.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByCategoryAndMonthAndYear(
            String category,
            Integer month,
            Integer year
    );
}