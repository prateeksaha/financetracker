package com.example.demo.service;

import com.example.demo.budget.service.BudgetService;
import com.example.demo.dto.TransactionRequestDTO;
import com.example.demo.dto.TransactionResponseDTO;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.specification.TransactionSpecification;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetService budgetService;

    public TransactionService(TransactionRepository transactionRepository,
                              BudgetService budgetService) {
        this.transactionRepository = transactionRepository;
        this.budgetService = budgetService;
    }

    // =========================
    // CREATE (WITH BUDGET STATUS)
    // =========================
    @Transactional
    @CacheEvict(
            value = "budgetStatusCache",
            key = "#request.category + '-' + T(java.time.LocalDateTime).now().getMonthValue() + '-' + T(java.time.LocalDateTime).now().getYear()"
    )
    public TransactionResponseDTO create(TransactionRequestDTO request) {

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction saved = transactionRepository.save(transaction);

        LocalDateTime now = LocalDateTime.now();

        String budgetStatus = budgetService.checkBudgetStatus(
                saved.getCategory(),
                now.getMonthValue(),
                now.getYear()
        );

        return new TransactionResponseDTO(
                saved.getId(),
                saved.getAmount(),
                saved.getCategory(),
                saved.getCreatedAt(),
                budgetStatus
        );
    }

    // =========================
    // LEGACY GET ALL (NO PAGINATION)
    // =========================
    public List<TransactionResponseDTO> getAll(
            String category,
            LocalDateTime from,
            LocalDateTime to
    ) {

        Specification<Transaction> spec =
                TransactionSpecification.hasCategory(category)
                        .and(TransactionSpecification.createdAfter(from))
                        .and(TransactionSpecification.createdBefore(to));

        return transactionRepository.findAll(spec)
                .stream()
                .map(t -> new TransactionResponseDTO(
                        t.getId(),
                        t.getAmount(),
                        t.getCategory(),
                        t.getCreatedAt(),
                        "N/A"
                ))
                .collect(Collectors.toList());
    }

    // =========================
    // PAGINATED VERSION (WITH FILTERS)
    // =========================
    public Page<TransactionResponseDTO> getAllPaged(
            String category,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size,
            String sortBy
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy == null ? "createdAt" : sortBy).descending()
        );

        Specification<Transaction> spec =
                TransactionSpecification.hasCategory(category)
                        .and(TransactionSpecification.createdAfter(from))
                        .and(TransactionSpecification.createdBefore(to));

        return transactionRepository.findAll(spec, pageable)
                .map(t -> new TransactionResponseDTO(
                        t.getId(),
                        t.getAmount(),
                        t.getCategory(),
                        t.getCreatedAt(),
                        "N/A"
                ));
    }
}