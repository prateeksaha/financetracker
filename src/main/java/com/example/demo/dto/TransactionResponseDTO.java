package com.example.demo.dto;

import java.time.LocalDateTime;

public class TransactionResponseDTO {

    private Long id;
    private Double amount;
    private String category;
    private LocalDateTime createdAt;
    private String budgetStatus;

    public TransactionResponseDTO(Long id, Double amount, String category, LocalDateTime createdAt,
                                  String budgetStatus) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.createdAt = createdAt;
        this.budgetStatus = budgetStatus;
    }

    public Long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getBudgetStatus() { return budgetStatus; }
}