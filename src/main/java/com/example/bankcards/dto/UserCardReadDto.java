package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UserCardReadDto(UUID id, String cardNumber, BigDecimal balance, LocalDate expirationDate) {}

