package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TransferRequestMapper implements Mapper<TransferRequestDto, Transaction> {

    private final CardRepository cardRepository;

    @Override
    public Transaction map(TransferRequestDto dto) {
        Transaction transaction = new Transaction();
        cardRepository.findById(dto.getFromCardId()).ifPresent(transaction::setSenderCard);
        cardRepository.findById(dto.getToCardId()).ifPresent(transaction::setReceiverCard);
        transaction.setAmount(dto.getAmount());
        transaction.setTransactionTime(LocalDateTime.now());
        return transaction;
    }
}

