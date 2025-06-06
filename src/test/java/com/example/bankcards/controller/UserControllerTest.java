package com.example.bankcards.controller;

import com.example.bankcards.dto.UserCreateEditDto;
import com.example.bankcards.dto.UserReadDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.security.JwtTokenFilter;
import com.example.bankcards.security.JwtTokenProvider;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // отключаем security фильтры для простоты
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenFilter jwtTokenFilter;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private final UUID userId = UUID.randomUUID();

    // Тест на получение пользователя по ID - нужен ADMIN
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void findById_returnsUser_whenExists() throws Exception {
        UserReadDto dto = buildUserReadDto();

        Mockito.when(userService.findById(userId)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void findById_returnsNotFound_whenMissing() throws Exception {
        Mockito.when(userService.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    // Обновление пользователя по id - ADMIN
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void update_returnsUpdatedUser_whenExists() throws Exception {
        UserCreateEditDto editDto = buildUserCreateDto();
        UserReadDto updatedDto = buildUserReadDto();

        Mockito.when(userService.update(eq(userId), any())).thenReturn(Optional.of(updatedDto));

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void update_returnsNotFound_whenMissing() throws Exception {
        UserCreateEditDto editDto = buildUserCreateDto();

        Mockito.when(userService.update(eq(userId), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editDto)))
                .andExpect(status().isNotFound());
    }

    // Удаление пользователя по id - ADMIN
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void delete_returnsNoContent_whenDeleted() throws Exception {
        Mockito.when(userService.delete(userId)).thenReturn(true);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void delete_returnsNotFound_whenMissing() throws Exception {
        Mockito.when(userService.delete(userId)).thenReturn(false);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    // --- Хелперы ---

    private UserReadDto buildUserReadDto() {
        return new UserReadDto(
                userId,
                "testuser",
                "test@example.com",
                Role.USER
        );
    }

    private UserCreateEditDto buildUserCreateDto() {
        return new UserCreateEditDto(
                "testuser",
                "test@example.com",
                "123456",
                Role.USER
        );
    }
}
