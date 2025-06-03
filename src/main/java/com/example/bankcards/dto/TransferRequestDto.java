package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class TransferRequestDto {
    @NotNull(message = "Source card ID must be specified")
    UUID fromCardId;

    @NotNull(message = "Destination card ID must be specified")
    UUID toCardId;

    @NotNull(message = "Amount must be specified")
    @DecimalMin(value = "0.01", message = "Amount must be positive and greater than zero")
    BigDecimal amount;
}