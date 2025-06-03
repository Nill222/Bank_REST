package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionCreateDto;
import com.example.bankcards.dto.TransactionReadDto;
import com.example.bankcards.dto.mapper.TransactionCreateMapper;
import com.example.bankcards.dto.mapper.TransactionReadMapper;
import com.example.bankcards.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionCreateMapper transactionCreateEditMapper;
    private final TransactionReadMapper transactionReadMapper;

    public Optional<TransactionReadDto> findById(UUID id) {
        return transactionRepository.findById(id)
                .map(transactionReadMapper::map);
    }

    @Transactional
    public TransactionReadDto create(TransactionCreateDto card) {
        return Optional.of(card)
                .map(transactionCreateEditMapper::map)
                .map(transactionRepository::save)
                .map(transactionReadMapper::map)
                .orElse(null);
    }
}