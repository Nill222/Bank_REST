package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionCreateDto;
import com.example.bankcards.dto.TransactionReadDto;
import com.example.bankcards.dto.mapper.TransactionCreateMapper;
import com.example.bankcards.dto.mapper.TransactionReadMapper;
import com.example.bankcards.exception.TransactionNotFoundException;
import com.example.bankcards.exception.TransactionOperationException;
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
        if (id == null) {
            throw new IllegalArgumentException("ID транзакции не может быть null");
        }

        return transactionRepository.findById(id)
                .map(transactionReadMapper::map)
                .or(() -> {
                    throw new TransactionNotFoundException(id);
                });
    }

    @Transactional
    public TransactionReadDto create(TransactionCreateDto transactionCreateDto) {
        if (transactionCreateDto == null) {
            throw new IllegalArgumentException("Данные транзакции не могут быть null");
        }

        return Optional.of(transactionCreateDto)
                .map(transactionCreateEditMapper::map)
                .map(transactionRepository::save)
                .map(transactionReadMapper::map)
                .orElseThrow(() -> new TransactionOperationException("Ошибка при создании транзакции"));
    }
}
