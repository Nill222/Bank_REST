package com.example.bankcards.service;

import com.example.bankcards.dto.TransferDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.util.CardStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransferService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private TransferRepository transferRepository;

    @Transactional
    public void transfer(TransferDTO dto) {
        Card from = cardRepository.findById(dto.fromCardId()).orElseThrow();
        Card to = cardRepository.findById(dto.toCardId()).orElseThrow();

        if (from.getStatus() != CardStatus.ACTIVE || to.getStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException("Card status invalid");
        }

        if (from.getBalance().compareTo(dto.amount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        from.setBalance(from.getBalance().subtract(dto.amount()));
        to.setBalance(to.getBalance().add(dto.amount()));

        Transfer transfer = new Transfer();
        transfer.setFromCard(from);
        transfer.setToCard(to);
        transfer.setAmount(dto.amount());
        transfer.setCreatedAt(LocalDateTime.now());

        transferRepository.save(transfer);
    }
}
