package com.example.bankcards.exception;

public class TransactionOperationException extends RuntimeException {
    public TransactionOperationException(String message) {
        super(message);
    }
}
