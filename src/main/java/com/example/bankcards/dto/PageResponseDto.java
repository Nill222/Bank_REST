package com.example.bankcards.dto;

import lombok.Value;

import java.util.List;

@Value
public class PageResponseDto<T> {
    List<T> content;
    int currentPage;
    int totalPages;
    long totalElements;
    int pageSize;
}