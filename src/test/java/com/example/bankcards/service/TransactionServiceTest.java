package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.dto.mapper.TransactionCreateMapper;
import com.example.bankcards.dto.mapper.TransactionReadMapper;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.exception.TransactionNotFoundException;
import com.example.bankcards.exception.TransactionOperationException;
import com.example.bankcards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionCreateMapper transactionCreateMapper;

    @Mock
    private TransactionReadMapper transactionReadMapper;

    @InjectMocks
    private TransactionService transactionService;

    private final UUID transactionId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById_shouldReturnTransactionReadDto_whenTransactionExists() {
        Transaction transaction = buildTransaction();
        TransactionReadDto expectedDto = buildTransactionReadDto();

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionReadMapper.map(transaction)).thenReturn(expectedDto);

        Optional<TransactionReadDto> result = transactionService.findById(transactionId);

        assertThat(result).isPresent().contains(expectedDto);
        verify(transactionRepository).findById(transactionId);
        verify(transactionReadMapper).map(transaction);
    }

    @Test
    void findById_shouldThrowTransactionNotFoundException_whenNotFound() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.findById(transactionId))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessageContaining(transactionId.toString());

        verify(transactionRepository).findById(transactionId);
        verifyNoInteractions(transactionReadMapper);
    }

    @Test
    void findById_shouldThrowIllegalArgumentException_whenIdIsNull() {
        assertThatThrownBy(() -> transactionService.findById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID транзакции не может быть null");

        verifyNoInteractions(transactionRepository, transactionReadMapper);
    }

    @Test
    void create_shouldSaveTransactionAndReturnDto() {
        TransactionCreateDto createDto = buildTransactionCreateDto();
        Transaction transaction = buildTransaction();
        TransactionReadDto expectedDto = buildTransactionReadDto();

        when(transactionCreateMapper.map(createDto)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(transactionReadMapper.map(transaction)).thenReturn(expectedDto);

        TransactionReadDto result = transactionService.create(createDto);

        assertThat(result).isEqualTo(expectedDto);
        verify(transactionCreateMapper).map(createDto);
        verify(transactionRepository).save(transaction);
        verify(transactionReadMapper).map(transaction);
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenDtoIsNull() {
        assertThatThrownBy(() -> transactionService.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Данные транзакции не могут быть null");

        verifyNoInteractions(transactionCreateMapper, transactionRepository, transactionReadMapper);
    }

    // ===== MOCK BUILDERS =====

    private Transaction buildTransaction() {
        return Transaction.builder()
                .senderCard(null) // упрощено
                .receiverCard(null)
                .amount(new BigDecimal("150.00"))
                .transactionTime(LocalDateTime.now())
                .build();
    }

    private TransactionReadDto buildTransactionReadDto() {
        return new TransactionReadDto(
                transactionId,
                buildSenderCardReadDto(),
                buildReceiverCardReadDto(),
                new BigDecimal("150.00"),
                LocalDateTime.now()
        );
    }

    private TransactionCreateDto buildTransactionCreateDto() {
        return new TransactionCreateDto(
                buildSenderCardReadDto(),
                buildReceiverCardReadDto(),
                new BigDecimal("150.00"),
                LocalDateTime.now()
        );
    }

    private CardReadDto buildSenderCardReadDto() {
        return new CardReadDto(
                UUID.randomUUID(),
                "1234-5678-9012-3456",
                new UserReadDto(UUID.randomUUID(), "senderUser", "sender@example.com", Role.USER),
                LocalDate.now().plusYears(2),
                CardStatus.ACTIVE,
                BigDecimal.valueOf(1000)
        );
    }

    private CardReadDto buildReceiverCardReadDto() {
        return new CardReadDto(
                UUID.randomUUID(),
                "9876-5432-1098-7654",
                new UserReadDto(UUID.randomUUID(), "receiverUser", "receiver@example.com", Role.USER),
                LocalDate.now().plusYears(2),
                CardStatus.ACTIVE,
                BigDecimal.valueOf(500)
        );
    }
}
