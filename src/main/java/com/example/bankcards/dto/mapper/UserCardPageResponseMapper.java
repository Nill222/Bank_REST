package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.PageResponseDto;
import com.example.bankcards.dto.UserCardReadDto;
import com.example.bankcards.entity.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserCardPageResponseMapper extends PageResponseMapper<Card, UserCardReadDto> {

    private final UserCardReadMapper mapper;

    public UserCardPageResponseMapper(UserCardReadMapper mapper) {
        super(mapper);
        this.mapper = mapper;
    }

    @Override
    public PageResponseDto<UserCardReadDto> map(Page<Card> page) {
        List<UserCardReadDto> content = page.getContent().stream()
                .map(mapper::map)
                .toList();

        return new PageResponseDto<>(
                content,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize()
        );
    }
}