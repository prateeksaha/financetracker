package com.example.demo.repository;

import com.example.demo.budget.dto.CategorySpendView;
import com.example.demo.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>,
        JpaSpecificationExecutor<Transaction> {

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE LOWER(t.category) = LOWER(:category)
        AND t.createdAt >= :start
        AND t.createdAt < :end
    """)
    Double getTotalSpent(
            @Param("category") String category,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
    SELECT LOWER(t.category) as category, COALESCE(SUM(t.amount), 0) as total
    FROM Transaction t
    WHERE t.createdAt >= :start
    AND t.createdAt < :end
    GROUP BY LOWER(t.category)
    """)
    List<CategorySpendView> getTotalSpentByCategory(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}