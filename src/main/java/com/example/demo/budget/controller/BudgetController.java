package com.example.demo.budget.controller;

import com.example.demo.budget.dto.BudgetAlertDTO;
import com.example.demo.budget.dto.BudgetRequestDTO;
import com.example.demo.budget.dto.BudgetResponseDTO;
import com.example.demo.budget.entity.Budget;
import com.example.demo.budget.service.BudgetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // CREATE BUDGET
    @PostMapping
    public BudgetResponseDTO create(@RequestBody BudgetRequestDTO request) {
        return budgetService.createBudget(request);
    }

    // GET BUDGET
    @GetMapping
    public Optional<Budget> getBudget(
            @RequestParam String category,
            @RequestParam int month,
            @RequestParam int year
    ) {
        return budgetService.getBudget(category, month, year);
    }

    // CHECK STATUS
    @GetMapping("/status")
    public String getStatus(
            @RequestParam String category,
            @RequestParam int month,
            @RequestParam int year
    ) {
        return budgetService.checkBudgetStatus(category, month, year);
    }

    // 🔥 MISSING ENDPOINT (THIS FIXES YOUR 404)
    @GetMapping("/alerts")
    public List<BudgetAlertDTO> getAlerts(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return budgetService.getExceededBudgets(month, year);
    }
}