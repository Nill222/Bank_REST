package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.TransactionCreateDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransactionCreateMapper implements Mapper<TransactionCreateDto, Transaction> {

    private final CardRepository cardRepository;

    @Override
    public Transaction map(TransactionCreateDto object) {
        Transaction transaction = new Transaction();
        copy(object, transaction);
        return transaction;
    }

    @Override
    public Transaction map(TransactionCreateDto fromObject, Transaction toObject) {
        copy(fromObject, toObject);
        return toObject;
    }

    private void copy(TransactionCreateDto object, Transaction transaction) {
        // Связь с картой-отправителем
        Card senderCard = Optional.ofNullable(object.getSenderCard())
                .flatMap(cardReadDto -> cardRepository.findById(cardReadDto.getId()))
                .orElse(null);
        transaction.setSenderCard(senderCard);

        // Связь с картой-получателем
        Card receiverCard = Optional.ofNullable(object.getReceiverCard())
                .flatMap(cardReadDto -> cardRepository.findById(cardReadDto.getId()))
                .orElse(null);
        transaction.setReceiverCard(receiverCard);

        transaction.setAmount(object.getAmount());
        transaction.setTransactionTime(object.getTransactionTime());
    }
}
