package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.UserCardReadDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.CardMaskUtil;
import org.springframework.stereotype.Component;

@Component
public class UserCardReadMapper implements Mapper<Card, UserCardReadDto>{
    @Override
    public UserCardReadDto map(Card card) {
        return new UserCardReadDto(
                card.getId(),
                CardMaskUtil.maskCardNumber(card.getCardNumber()),
                card.getBalance(),
                card.getExpirationDate()
        );
    }
}
