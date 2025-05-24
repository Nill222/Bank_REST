package com.example.bankcards.dto;

public record RegisterRequestDTO(String username,
                                 String password,
                                 String fullName) {
}