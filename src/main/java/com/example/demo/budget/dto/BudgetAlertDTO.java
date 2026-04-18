package com.example.demo.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BudgetAlertDTO {
    private String category;
    private Double budgetLimit;
    private Double spent;
    private Double exceededBy;
}