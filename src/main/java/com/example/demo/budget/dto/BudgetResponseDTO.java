package com.example.demo.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BudgetResponseDTO {
    private Long id;
    private String category;
    private Double monthlyLimit;
    private Integer month;
    private Integer year;
}