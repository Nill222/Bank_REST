package com.example.bankcards.security;

import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final CardService cardService;

    public boolean isCardOwner(Authentication authentication, UUID cardId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String currentUsername = authentication.getName();
        try {
            return cardService.isOwner(cardId, currentUsername);
        } catch (Exception e) {
            log.warn("Ошибка при проверке владельца карты: {}", e.getMessage());
            return false;
        }
    }

    public boolean canTransfer(Authentication authentication, UUID fromCardId, UUID toCardId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        try {
            return cardService.isOwner(fromCardId, username) && cardService.isOwner(toCardId, username);
        } catch (Exception e) {
            log.warn("Ошибка при проверке прав на перевод: {}", e.getMessage());
            return false;
        }
    }
}
