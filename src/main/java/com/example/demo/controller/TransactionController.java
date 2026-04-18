package com.example.demo.controller;

import com.example.demo.dto.TransactionRequestDTO;
import com.example.demo.dto.TransactionResponseDTO;
import com.example.demo.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public TransactionResponseDTO create(@Valid @RequestBody TransactionRequestDTO request) {
        return service.create(request);
    }

    @GetMapping
    public List<TransactionResponseDTO> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to
    ) {
        return service.getAll(category, from, to);
    }

    @GetMapping("/paged")
    public Page<TransactionResponseDTO> getAllPaged(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        return service.getAllPaged(category, from, to, page, size, sortBy);
    }
}