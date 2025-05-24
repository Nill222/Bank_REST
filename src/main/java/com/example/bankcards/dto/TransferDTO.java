package com.example.bankcards.dto;

import java.math.BigDecimal;

public record TransferDTO(Long fromCardId,
                          Long toCardId,
                          BigDecimal amount) {
}