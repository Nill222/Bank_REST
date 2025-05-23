package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "transfer")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
