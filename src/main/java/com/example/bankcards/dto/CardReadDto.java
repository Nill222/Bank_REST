package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.Data;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Value
public class CardReadDto {
    UUID id;
    String cardNumber;
    UserReadDto owner;
    LocalDate expirationDate;
    CardStatus status;
    BigDecimal balance;
}