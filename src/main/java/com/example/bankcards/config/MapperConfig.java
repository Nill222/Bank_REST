package com.example.bankcards.config;

import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.dto.UserReadDto;
import com.example.bankcards.dto.mapper.CardReadMapper;
import com.example.bankcards.dto.mapper.PageResponseMapper;
import com.example.bankcards.dto.mapper.UserReadMapper;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public PageResponseMapper<Card, CardReadDto> cardPageResponseMapper(CardReadMapper cardReadMapper) {
        return new PageResponseMapper<>(cardReadMapper);
    }

    @Bean
    public PageResponseMapper<User, UserReadDto> userPageResponseMapper(UserReadMapper userReadMapper) {
        return new PageResponseMapper<>(userReadMapper);
    }
}
