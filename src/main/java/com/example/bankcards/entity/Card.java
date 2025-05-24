package com.example.bankcards.entity;

import com.example.bankcards.util.CardStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "masked_number", nullable = false)
    private String maskedNumber;

    @Column(name = "expiration", nullable = false)
    private LocalDate expiration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    public Card() {
    }

    public Card(Long id, String number, String maskedNumber, LocalDate expiration,
                CardStatus status, BigDecimal balance, User owner) {
        this.id = id;
        this.number = number;
        this.maskedNumber = maskedNumber;
        this.expiration = expiration;
        this.status = status;
        this.balance = balance;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMaskedNumber() {
        return maskedNumber;
    }

    public void setMaskedNumber(String maskedNumber) {
        this.maskedNumber = maskedNumber;
    }

    public LocalDate getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDate expiration) {
        this.expiration = expiration;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(id, card.id)
                && Objects.equals(number, card.number)
                && Objects.equals(maskedNumber, card.maskedNumber)
                && Objects.equals(expiration, card.expiration)
                && status == card.status && Objects.equals(balance, card.balance)
                && Objects.equals(owner, card.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, maskedNumber, expiration, status, balance, owner);
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", maskedNumber='" + maskedNumber + '\'' +
                ", expiration=" + expiration +
                ", status=" + status +
                ", balance=" + balance +
                ", owner=" + owner +
                '}';
    }
}
