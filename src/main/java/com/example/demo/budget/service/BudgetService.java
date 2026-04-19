package com.example.demo.budget.service;

import com.example.demo.budget.dto.BudgetAlertDTO;
import com.example.demo.budget.dto.BudgetRequestDTO;
import com.example.demo.budget.dto.BudgetResponseDTO;
import com.example.demo.budget.dto.CategorySpendView;
import com.example.demo.budget.entity.Budget;
import com.example.demo.budget.repository.BudgetRepository;
import com.example.demo.repository.TransactionRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(BudgetRepository budgetRepository,
                         TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    // 1. CREATE BUDGET
    public BudgetResponseDTO createBudget(BudgetRequestDTO request) {

        Budget budget = new Budget();
        budget.setCategory(request.getCategory());
        budget.setMonthlyLimit(request.getMonthlyLimit());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());

        Budget saved = budgetRepository.save(budget);

        return new BudgetResponseDTO(
                saved.getId(),
                saved.getCategory(),
                saved.getMonthlyLimit(),
                saved.getMonth(),
                saved.getYear()
        );
    }

    // 2. GET BUDGET
    public Optional<Budget> getBudget(String category, int month, int year) {
        return budgetRepository.findByCategoryAndMonthAndYear(
                category, month, year
        );
    }

    // 3. CORE LOGIC → calculate spending
    public Double getTotalSpent(String category, int month, int year) {

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);

        return transactionRepository.getTotalSpent(category, start, end);
    }

    // 4. CHECK BUDGET STATUS (IMPORTANT)
    // to do: need to check for concurrency
    @Transactional(readOnly = true)
    @Cacheable(
            value = "budgetStatusCache",
            key = "#category + '-' + #month + '-' + #year"
    )
    public String checkBudgetStatus(String category, int month, int year) {

        Optional<Budget> budgetOpt =
                getBudget(category, month, year);

        if (budgetOpt.isEmpty()) {
            return "NO BUDGET SET";
        }

        Budget budget = budgetOpt.get();

        Double spent = getTotalSpent(category, month, year);

        if (spent > budget.getMonthlyLimit()) {
            return "EXCEEDED by " + (spent - budget.getMonthlyLimit());
        }

        return "WITHIN LIMIT. Remaining: " +
                (budget.getMonthlyLimit() - spent);
    }

    public List<BudgetAlertDTO> getExceededBudgets(int month, int year) {

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);

        // ✅ ONE DB CALL
        List<CategorySpendView> spendList =
                transactionRepository.getTotalSpentByCategory(start, end);

        // Convert list → Map for fast lookup
        Map<String, Double> spendMap = spendList.stream()
                .collect(Collectors.toMap(
                        CategorySpendView::getCategory,
                        CategorySpendView::getTotal
                ));

        // Still one query (acceptable)
        List<Budget> budgets = budgetRepository.findAll();

        return budgets.stream()
                .map(budget -> {

                    Double spent = spendMap.getOrDefault(
                            budget.getCategory().toLowerCase(),
                            0.0
                    );

                    if (spent > budget.getMonthlyLimit()) {
                        return new BudgetAlertDTO(
                                budget.getCategory(),
                                budget.getMonthlyLimit(),
                                spent,
                                spent - budget.getMonthlyLimit()
                        );
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
