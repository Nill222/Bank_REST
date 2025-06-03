package com.example.bankcards.dto;


import com.example.bankcards.entity.Role;
import lombok.Value;

import java.util.UUID;

@Value
public class UserReadDto {
    UUID id;
    String username;
    String email;
    Role role;
}