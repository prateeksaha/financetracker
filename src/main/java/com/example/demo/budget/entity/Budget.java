package com.example.demo.budget.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    @Column(name = "monthly_limit")
    private Double monthlyLimit;

    private Integer month;   // 1-12

    private Integer year;
}
