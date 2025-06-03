package com.example.bankcards.dto;

import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class TransactionReadDto {
    UUID id;
    CardReadDto senderCard;
    CardReadDto receiverCard;
    BigDecimal amount;
    LocalDateTime transactionTime;
}