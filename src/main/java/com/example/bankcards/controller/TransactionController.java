package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionCreateDto;
import com.example.bankcards.dto.TransactionReadDto;
import com.example.bankcards.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionReadDto> findById(@PathVariable UUID id) {
        return transactionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TransactionReadDto> create(@RequestBody @Valid TransactionCreateDto dto) {
        TransactionReadDto createdTransaction = transactionService.create(dto);
        return ResponseEntity.ok(createdTransaction);
    }
}
