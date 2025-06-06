package com.example.bankcards.exception;

import java.util.UUID;

public class CardAccessDeniedException extends RuntimeException {
    public CardAccessDeniedException(UUID cardId) {
        super("Карта с id " + cardId + " не найдена или доступ запрещён");
    }
}

