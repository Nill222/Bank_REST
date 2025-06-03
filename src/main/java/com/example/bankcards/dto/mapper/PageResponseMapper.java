package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.PageResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;


public class PageResponseMapper<S, T> {

    private final Mapper<S, T> contentMapper;

    public PageResponseMapper(Mapper<S, T> contentMapper) {
        this.contentMapper = contentMapper;
    }

    public PageResponseDto<T> map(Page<S> page) {
        List<T> content = page.getContent()
                .stream()
                .map(contentMapper::map)
                .collect(Collectors.toList());

        return new PageResponseDto<>(
                content,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize()
        );
    }
}