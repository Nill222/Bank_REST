package com.example.bankcards.dto;

import com.example.bankcards.util.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardDTO(Long id,
                      String maskedNumber,
                      LocalDate expiration,
                      CardStatus status,
                      BigDecimal balance) {
}