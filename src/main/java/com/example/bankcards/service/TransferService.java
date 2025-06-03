package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.dto.mapper.TransferRequestMapper;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final TransferRequestMapper transferRequestMapper;

    @Transactional
    public void transfer(TransferRequestDto dto, UUID userId) {

        Card from = cardRepository.findById(dto.getFromCardId())
                .filter(c -> c.getOwner().getId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Source card not found or access denied"));

        Card to = cardRepository.findById(dto.getToCardId())
                .filter(c -> c.getOwner().getId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Destination card not found or access denied"));

        if (from.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(dto.getAmount()));
        to.setBalance(to.getBalance().add(dto.getAmount()));

        Transaction transaction = transferRequestMapper.map(dto);
        transaction.setSenderCard(from);
        transaction.setReceiverCard(to);
        transaction.setTransactionTime(LocalDateTime.now());

        transactionRepository.save(transaction);
        cardRepository.save(from);
        cardRepository.save(to);
    }
}
