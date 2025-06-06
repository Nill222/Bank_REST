package com.example.bankcards.exception;

import java.util.UUID;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(UUID id) {
        super("Транзакция с id " + id + " не найдена");
    }
}

