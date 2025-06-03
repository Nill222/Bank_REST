package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class TransactionCreateDto {
    @NotNull(message = "Sender card must not be null")
    CardReadDto senderCard;

    @NotNull
    CardReadDto receiverCard;

    @NotNull
    @Positive
    BigDecimal amount;

    @NotNull
    LocalDateTime transactionTime;
}