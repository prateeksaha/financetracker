package com.example.demo.service;

import com.example.demo.dto.TransactionRequestDTO;
import com.example.demo.dto.TransactionResponseDTO;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.specification.TransactionSpecification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public TransactionResponseDTO create(TransactionRequestDTO request) {

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction saved = repository.save(transaction);

        return mapToDTO(saved);
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

        return repository.findAll(spec)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // =========================
    // NEW PAGINATED VERSION
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

        return repository.findAll(spec, pageable)
                .map(this::mapToDTO);
    }

    // MAPPER
    private TransactionResponseDTO mapToDTO(Transaction t) {
        return new TransactionResponseDTO(
                t.getId(),
                t.getAmount(),
                t.getCategory(),
                t.getCreatedAt()
        );
    }
}