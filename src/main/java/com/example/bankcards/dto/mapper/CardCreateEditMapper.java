package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.CardCreateEditDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardCreateEditMapper implements Mapper<CardCreateEditDto, Card> {

    private final UserRepository userRepository;

    @Override
    public Card map(CardCreateEditDto object) {
        Card card = new Card();
        copy(object, card);
        return card;
    }

    @Override
    public Card map(CardCreateEditDto fromObject, Card toObject) {
        copy(fromObject, toObject);
        return toObject;
    }

    private void copy(CardCreateEditDto object, Card card) {
        card.setCardNumber(object.getCardNumber());
        card.setExpirationDate(object.getExpirationDate());
        card.setStatus(object.getStatus());
        card.setBalance(object.getBalance());

        if (object.getOwner() != null) {
            User owner = userRepository.findById(object.getOwner())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id " + object.getOwner()));
            card.setOwner(owner);
        } else {
            throw new IllegalArgumentException("Owner ID must not be null");
        }
    }
}
