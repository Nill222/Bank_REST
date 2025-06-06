package com.example.bankcards.controller;

import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.dto.TransactionCreateDto;
import com.example.bankcards.dto.TransactionReadDto;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID transactionId;
    private TransactionReadDto transactionReadDto;
    private TransactionCreateDto transactionCreateDto;

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();

        CardReadDto senderCard = new CardReadDto(
                UUID.randomUUID(),
                "1111-2222-3333-4444",
                null,
                null,
                CardStatus.ACTIVE,
                BigDecimal.valueOf(5000)
        );

        CardReadDto receiverCard = new CardReadDto(
                UUID.randomUUID(),
                "5555-6666-7777-8888",
                null,
                null,
                CardStatus.ACTIVE,
                BigDecimal.valueOf(3000)
        );

        transactionReadDto = new TransactionReadDto(
                transactionId,
                senderCard,
                receiverCard,
                BigDecimal.valueOf(1000),
                LocalDateTime.now()
        );

        transactionCreateDto = new TransactionCreateDto(
                senderCard,
                receiverCard,
                BigDecimal.valueOf(1000),
                LocalDateTime.now()
        );
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void findById_ShouldReturnTransaction_WhenExists() throws Exception {
        Mockito.when(transactionService.findById(transactionId))
                .thenReturn(Optional.of(transactionReadDto));

        mockMvc.perform(get("/api/transactions/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.amount").value(1000));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void findById_ShouldReturnNotFound_WhenNotExists() throws Exception {
        Mockito.when(transactionService.findById(transactionId))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/transactions/{id}", transactionId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser // по умолчанию роль USER, но здесь роль не проверяется
    void create_ShouldReturnCreatedTransaction() throws Exception {
        Mockito.when(transactionService.create(any(TransactionCreateDto.class)))
                .thenReturn(transactionReadDto);

        String json = objectMapper.writeValueAsString(transactionCreateDto);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.amount").value(1000));
    }
}
