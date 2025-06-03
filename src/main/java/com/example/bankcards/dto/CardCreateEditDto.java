package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Value
public class CardCreateEditDto {
    @NotBlank
    String cardNumber;

    @NotNull
    UUID owner;

    @NotNull
    LocalDate expirationDate;

    @NotNull
    CardStatus status;

    @NotNull
            @PositiveOrZero
    BigDecimal balance;
}