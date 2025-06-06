package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateEditDto;
import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.dto.mapper.CardCreateEditMapper;
import com.example.bankcards.dto.mapper.CardReadMapper;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardCreateEditMapper cardCreateEditMapper;

    @Mock
    private CardReadMapper cardReadMapper;

    @InjectMocks
    private CardService cardService;

    private Card card;
    private UUID cardId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        User owner = new User();
        owner.setUsername("testUser");
        cardId = UUID.randomUUID();

        card = Card.builder()
                .cardNumber("1234-5678-9012-3456")
                .owner(owner)
                .expirationDate(LocalDate.now().plusYears(1))
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.valueOf(1000))
                .build();
    }

    @Test
    void findMaskedByOwnerAndCardNumber_ownerNull_throwsException() {
        assertThatThrownBy(() -> cardService.findMaskedByOwnerAndCardNumber(null, "123", Pageable.unpaged()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Владелец карты не может быть null");
    }

    @Test
    void changeCardStatus_cardNotFound_throwsException() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.changeCardStatus(cardId, CardStatus.BLOCKED))
                .isInstanceOf(CardNotFoundException.class)
                .hasMessageContaining(cardId.toString());
    }

    @Test
    void changeCardStatus_statusAlreadySet_throwsException() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> cardService.changeCardStatus(cardId, CardStatus.ACTIVE))
                .isInstanceOf(CardOperationException.class)
                .hasMessageContaining("Статус карты уже установлен");
    }

    @Test
    void changeCardStatus_success() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(cardReadMapper.map(any())).thenReturn(new CardReadDto(cardId, "1234-5678-9012-3456", null, card.getExpirationDate(), CardStatus.BLOCKED, BigDecimal.valueOf(1000)));

        CardReadDto result = cardService.changeCardStatus(cardId, CardStatus.BLOCKED);

        assertThat(result.getStatus()).isEqualTo(CardStatus.BLOCKED);
        verify(cardRepository).save(card);
    }

    @Test
    void findById_nullId_throwsException() {
        assertThatThrownBy(() -> cardService.findById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID карты не может быть null");
    }

    @Test
    void findById_found_returnsDto() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardReadMapper.map(card)).thenReturn(new CardReadDto(cardId, card.getCardNumber(), null, card.getExpirationDate(), card.getStatus(), card.getBalance()));

        Optional<CardReadDto> result = cardService.findById(cardId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(cardId);
    }

    @Test
    void create_nullCard_throwsException() {
        assertThatThrownBy(() -> cardService.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Данные карты не могут быть null");
    }

    @Test
    void create_success() {
        CardCreateEditDto createDto = mock(CardCreateEditDto.class);
        Card cardMapped = card;
        when(cardCreateEditMapper.map(createDto)).thenReturn(cardMapped);
        when(cardRepository.save(cardMapped)).thenReturn(cardMapped);
        when(cardReadMapper.map(cardMapped)).thenReturn(new CardReadDto(cardId, card.getCardNumber(), null, card.getExpirationDate(), card.getStatus(), card.getBalance()));

        CardReadDto result = cardService.create(createDto);

        assertThat(result).isNotNull();
        verify(cardRepository).save(cardMapped);
    }

    @Test
    void update_nullId_throwsException() {
        assertThatThrownBy(() -> cardService.update(null, mock(CardCreateEditDto.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID карты не может быть null");
    }

    @Test
    void update_nullDto_throwsException() {
        assertThatThrownBy(() -> cardService.update(cardId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Данные карты не могут быть null");
    }

    @Test
    void update_success() {
        CardCreateEditDto updateDto = mock(CardCreateEditDto.class);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardCreateEditMapper.map(updateDto, card)).thenReturn(card);
        when(cardRepository.saveAndFlush(card)).thenReturn(card);
        when(cardReadMapper.map(card)).thenReturn(new CardReadDto(cardId, card.getCardNumber(), null, card.getExpirationDate(), card.getStatus(), card.getBalance()));

        Optional<CardReadDto> result = cardService.update(cardId, updateDto);

        assertThat(result).isPresent();
        verify(cardRepository).saveAndFlush(card);
    }

    @Test
    void delete_nullId_throwsException() {
        assertThatThrownBy(() -> cardService.delete(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID карты не может быть null");
    }

    @Test
    void delete_cardNotFound_throwsException() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.delete(cardId))
                .isInstanceOf(CardNotFoundException.class)
                .hasMessageContaining(cardId.toString());
    }

    @Test
    void delete_success() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        boolean result = cardService.delete(cardId);

        assertThat(result).isTrue();
        verify(cardRepository).delete(card);
        verify(cardRepository).flush();
    }

    @Test
    void isOwner_usernameMatches_returnsTrue() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        boolean result = cardService.isOwner(cardId, "testUser");
        assertThat(result).isTrue();
    }

    @Test
    void isOwner_usernameDoesNotMatch_returnsFalse() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        boolean result = cardService.isOwner(cardId, "otherUser");
        assertThat(result).isFalse();
    }
}
