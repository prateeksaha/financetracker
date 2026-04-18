package com.example.demo.budget.service;

import com.example.demo.budget.dto.BudgetAlertDTO;
import com.example.demo.budget.dto.BudgetRequestDTO;
import com.example.demo.budget.dto.BudgetResponseDTO;
import com.example.demo.budget.entity.Budget;
import com.example.demo.budget.repository.BudgetRepository;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

        List<Transaction> transactions =
                transactionRepository.findAll();

        return transactions.stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .filter(t -> !t.getCreatedAt().isBefore(start))
                .filter(t -> t.getCreatedAt().isBefore(end))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // 4. CHECK BUDGET STATUS (IMPORTANT)
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

        List<Budget> budgets = budgetRepository.findAll();

        return budgets.stream()
                .map(budget -> {

                    LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
                    LocalDateTime end = start.plusMonths(1);

                    Double spent = getTotalSpent(
                            budget.getCategory(),
                            month,
                            year
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
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}
