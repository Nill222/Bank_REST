package com.example.bankcards.security;

import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final CardService cardService;

    public boolean isCardOwner(Authentication authentication, UUID cardId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String currentUsername = authentication.getName();
        return cardService.isOwner(cardId, currentUsername);
    }

    public boolean canTransfer(Authentication authentication, UUID fromCardId, UUID toCardId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        try {
            return cardService.isOwner(fromCardId, username) && cardService.isOwner(toCardId, username);
        } catch (Exception e) {
            return false;
        }
    }
}
