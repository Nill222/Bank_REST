package com.example.bankcards.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "transfer")
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_card_id")
    private Card fromCard;

    @ManyToOne
    @JoinColumn(name = "to_card_id")
    private Card toCard;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Transfer() {
    }

    public Transfer(Long id, Card fromCard, Card toCard, BigDecimal amount, LocalDateTime createdAt) {
        this.id = id;
        this.fromCard = fromCard;
        this.toCard = toCard;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Card getFromCard() {
        return fromCard;
    }

    public void setFromCard(Card fromCard) {
        this.fromCard = fromCard;
    }

    public Card getToCard() {
        return toCard;
    }

    public void setToCard(Card toCard) {
        this.toCard = toCard;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return Objects.equals(id, transfer.id)
                && Objects.equals(fromCard, transfer.fromCard)
                && Objects.equals(toCard, transfer.toCard)
                && Objects.equals(amount, transfer.amount)
                && Objects.equals(createdAt, transfer.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromCard, toCard, amount, createdAt);
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", fromCard=" + fromCard +
                ", toCard=" + toCard +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                '}';
    }
}
