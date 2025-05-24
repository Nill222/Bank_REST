package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    public CardDTO createCard(String fullCardNumber, LocalDate expiration) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByUsername(username).orElseThrow();

        Card card = new Card();
        card.setNumber(fullCardNumber);
        card.setMaskedNumber(maskCardNumber(fullCardNumber));
        card.setExpiration(expiration);
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);
        card.setOwner(owner);

        cardRepository.save(card);
        return toDTO(card);
    }

    public List<CardDTO> getMyCards() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByUsername(username).orElseThrow();
        return owner.getCards().stream().map(this::toDTO).toList();
    }

    public void deleteCard(Long id) {
        cardRepository.deleteById(id);
    }

    private String maskCardNumber(String number) {
        return "**** **** **** " + number.substring(number.length() - 4);
    }

    private CardDTO toDTO(Card card) {
        return new CardDTO(
                card.getId(),
                card.getMaskedNumber(),
                card.getExpiration(),
                card.getStatus(),
                card.getBalance()
        );
    }
}
