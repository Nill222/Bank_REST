package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateEditDto;
import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.dto.PageResponseDto;
import com.example.bankcards.dto.UserCardReadDto;
import com.example.bankcards.dto.mapper.CardCreateEditMapper;
import com.example.bankcards.dto.mapper.CardReadMapper;
import com.example.bankcards.dto.mapper.PageResponseMapper;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.exception.InvalidCardStatusException;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {
    private final CardRepository cardRepository;
    private final CardCreateEditMapper cardCreateEditMapper;
    private final CardReadMapper cardReadMapper;
    private final PageResponseMapper<Card, CardReadDto> cardPageResponseMapper;
    private final PageResponseMapper<Card, UserCardReadDto> userCardPageResponseMapper;

    public PageResponseDto<UserCardReadDto> findMaskedByOwnerAndCardNumber(User owner, String cardNumber, Pageable pageable) {
        if (owner == null) {
            throw new IllegalArgumentException("Владелец карты не может быть null");
        }

        Page<Card> page = cardRepository.findByOwnerAndCardNumberContainingIgnoreCase(
                owner,
                Optional.ofNullable(cardNumber).orElse(""),
                Optional.ofNullable(pageable).orElse(Pageable.ofSize(10))
        );

        return userCardPageResponseMapper.map(page);
    }


    public PageResponseDto<CardReadDto> findByOwnerAndCardNumber(User owner, String cardNumber, Pageable pageable) {
        if (owner == null) {
            throw new IllegalArgumentException("Владелец карты не может быть null");
        }

        Page<Card> page = cardRepository.findByOwnerAndCardNumberContainingIgnoreCase(
                owner,
                Optional.ofNullable(cardNumber).orElse(""),
                Optional.ofNullable(pageable).orElse(Pageable.ofSize(10))
        );

        return cardPageResponseMapper.map(page);
    }

    @Transactional
    public CardReadDto changeCardStatus(UUID cardId, CardStatus newStatus) {
        if (newStatus == null) {
            throw new InvalidCardStatusException("null");
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        if (card.getStatus() == newStatus) {
            throw new CardOperationException("Статус карты уже установлен в " + newStatus);
        }

        card.setStatus(newStatus);
        Card saved = cardRepository.save(card);
        return cardReadMapper.map(saved);
    }

    public Optional<CardReadDto> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID карты не может быть null");
        }
        return cardRepository.findById(id)
                .map(cardReadMapper::map);
    }

    @Transactional
    public CardReadDto create(CardCreateEditDto card) {
        if (card == null) {
            throw new IllegalArgumentException("Данные карты не могут быть null");
        }

        return Optional.of(card)
                .map(cardCreateEditMapper::map)
                .map(cardRepository::save)
                .map(cardReadMapper::map)
                .orElseThrow(() -> new CardOperationException("Ошибка при создании карты"));
    }

    @Transactional
    public Optional<CardReadDto> update(UUID id, CardCreateEditDto card) {
        if (id == null) {
            throw new IllegalArgumentException("ID карты не может быть null");
        }
        if (card == null) {
            throw new IllegalArgumentException("Данные карты не могут быть null");
        }

        return cardRepository.findById(id)
                .map(entity -> cardCreateEditMapper.map(card, entity))
                .map(cardRepository::saveAndFlush)
                .map(cardReadMapper::map);
    }

    @Transactional
    public boolean delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID карты не может быть null");
        }

        return cardRepository.findById(id)
                .map(entity -> {
                    cardRepository.delete(entity);
                    cardRepository.flush();
                    return true;
                }).orElseThrow(() -> new CardNotFoundException(id));
    }

    public boolean isOwner(UUID cardId, String username) {
        return cardRepository.findById(cardId)
                .map(card -> card.getOwner().getUsername().equals(username))
                .orElse(false);
    }
}