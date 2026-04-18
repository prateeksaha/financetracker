package com.example.demo.budget.dto;

import lombok.Data;

@Data
public class BudgetRequestDTO {
    private String category;
    private Double monthlyLimit;
    private Integer month;
    private Integer year;
}