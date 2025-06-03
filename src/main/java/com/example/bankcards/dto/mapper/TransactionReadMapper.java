package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.dto.TransactionReadDto;
import com.example.bankcards.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransactionReadMapper implements Mapper<Transaction, TransactionReadDto> {

    private final CardReadMapper cardReadMapper;

    @Override
    public TransactionReadDto map(Transaction transaction) {
        CardReadDto senderCard = Optional.ofNullable(transaction.getSenderCard())
                .map(cardReadMapper::map)
                .orElse(null);

        CardReadDto receiverCard = Optional.ofNullable(transaction.getReceiverCard())
                .map(cardReadMapper::map)
                .orElse(null);

        return new TransactionReadDto(
                transaction.getId(),
                senderCard,
                receiverCard,
                transaction.getAmount(),
                transaction.getTransactionTime()
        );
    }
}