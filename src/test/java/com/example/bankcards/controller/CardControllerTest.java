package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateEditDto;
import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import com.example.bankcards.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private SecurityService securityService;

    private UUID cardId;
    private CardReadDto cardReadDto;
    private CardCreateEditDto cardCreateEditDto;

    @BeforeEach
    void setUp() {
        cardId = UUID.randomUUID();

        cardReadDto = new CardReadDto(
                cardId,
                "1234-5678-9012-3456",
                null, // owner - упрощаем, можно добавить при необходимости
                LocalDate.now().plusYears(1),
                CardStatus.ACTIVE,
                BigDecimal.valueOf(1000)
        );

        cardCreateEditDto = new CardCreateEditDto(
                "1234-5678-9012-3456",
                UUID.randomUUID(),
                LocalDate.now().plusYears(1),
                CardStatus.ACTIVE,
                BigDecimal.valueOf(1000)
        );
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void findById_ShouldReturnCard_WhenCardExists() throws Exception {
        Mockito.when(cardService.findById(cardId)).thenReturn(Optional.of(cardReadDto));

        mockMvc.perform(get("/api/cards/{id}", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()))
                .andExpect(jsonPath("$.cardNumber").value("1234-5678-9012-3456"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void findById_ShouldReturnNotFound_WhenCardDoesNotExist() throws Exception {
        Mockito.when(cardService.findById(cardId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/cards/{id}", cardId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void create_ShouldReturnCreatedCard() throws Exception {
        Mockito.when(cardService.create(any(CardCreateEditDto.class))).thenReturn(cardReadDto);

        String json = """
            {
              "cardNumber": "1234-5678-9012-3456",
              "owner": "%s",
              "expirationDate": "%s",
              "status": "ACTIVE",
              "balance": 1000
            }
            """.formatted(
                cardCreateEditDto.getOwner().toString(),
                cardCreateEditDto.getExpirationDate()
        );

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value("1234-5678-9012-3456"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void update_ShouldReturnUpdatedCard_WhenCardExists() throws Exception {
        Mockito.when(cardService.update(eq(cardId), any(CardCreateEditDto.class)))
                .thenReturn(Optional.of(cardReadDto));

        String json = """
            {
              "cardNumber": "1234-5678-9012-3456",
              "owner": "%s",
              "expirationDate": "%s",
              "status": "ACTIVE",
              "balance": 1000
            }
            """.formatted(
                cardCreateEditDto.getOwner().toString(),
                cardCreateEditDto.getExpirationDate()
        );

        mockMvc.perform(put("/api/cards/{id}", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void update_ShouldReturnNotFound_WhenCardDoesNotExist() throws Exception {
        Mockito.when(cardService.update(eq(cardId), any(CardCreateEditDto.class)))
                .thenReturn(Optional.empty());

        String json = """
            {
              "cardNumber": "1234-5678-9012-3456",
              "owner": "%s",
              "expirationDate": "%s",
              "status": "ACTIVE",
              "balance": 1000
            }
            """.formatted(
                cardCreateEditDto.getOwner().toString(),
                cardCreateEditDto.getExpirationDate()
        );

        mockMvc.perform(put("/api/cards/{id}", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void delete_ShouldReturnNoContent_WhenDeleted() throws Exception {
        Mockito.when(cardService.delete(cardId)).thenReturn(true);

        mockMvc.perform(delete("/api/cards/{id}", cardId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void delete_ShouldReturnNotFound_WhenCardNotFound() throws Exception {
        Mockito.when(cardService.delete(cardId)).thenReturn(false);

        mockMvc.perform(delete("/api/cards/{id}", cardId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void changeStatus_ShouldReturnUpdatedCard() throws Exception {
        Mockito.when(cardService.changeCardStatus(cardId, CardStatus.BLOCKED)).thenReturn(cardReadDto);

        mockMvc.perform(patch("/api/cards/{id}/status", cardId)
                        .param("status", "BLOCKED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()));
    }
}
