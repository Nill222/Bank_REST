package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.dto.UserReadDto;
import com.example.bankcards.entity.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CardReadMapper implements Mapper<Card, CardReadDto> {

    private final UserReadMapper userReadMapper;

    @Override
    public CardReadDto map(Card card) {
        UserReadDto owner = Optional.ofNullable(card.getOwner())
                .map(userReadMapper::map)
                .orElse(null);

        return new CardReadDto(
                card.getId(),
                card.getCardNumber(),
                owner,
                card.getExpirationDate(),
                card.getStatus(),
                card.getBalance()
        );
    }
}