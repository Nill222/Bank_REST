package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateEditDto;
import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.dto.PageResponseDto;
import com.example.bankcards.dto.UserCardReadDto;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CardReadDto> findById(@PathVariable UUID id) {
        return cardService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<CardReadDto> create(@RequestBody @Valid CardCreateEditDto dto) {
        CardReadDto created = cardService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CardReadDto> update(@PathVariable UUID id,
                                              @RequestBody @Valid CardCreateEditDto dto) {
        return cardService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        boolean deleted = cardService.delete(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isCardOwner(authentication, #id)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<CardReadDto> changeStatus(@PathVariable UUID id,
                                                    @RequestParam CardStatus status) {
        return ResponseEntity.ok(cardService.changeCardStatus(id, status));
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<UserCardReadDto>> findMaskedByOwnerAndCardNumber(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String cardNumber,
            Pageable pageable) {
        PageResponseDto<UserCardReadDto> result = cardService.findMaskedByOwnerAndCardNumber(user, cardNumber, pageable);
        return ResponseEntity.ok(result);
    }
}
