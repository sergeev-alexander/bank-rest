package com.example.bankcards.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a money transfer between cards.
 * Records transfers from one card to another with status tracking.
 * Automatically sets creation timestamp and default status.
 *
 * @author Bank System Team
 * @since 1.0.0
 */
@Entity
@Table(name = "transfers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"fromCard", "toCard"})
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_card_id", nullable = false)
    private Card fromCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_card_id", nullable = false)
    private Card toCard;

    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Constructor for creating a transfer with automatic timestamp.
     *
     * @param fromCard source card
     * @param toCard destination card
     * @param amount transfer amount
     * @param status transfer status
     */
    public Transfer(Card fromCard, Card toCard, BigDecimal amount, TransferStatus status) {
        this.fromCard = fromCard;
        this.toCard = toCard;
        this.amount = amount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Sets creation timestamp and default status before persisting.
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = TransferStatus.PENDING;
        }
    }
}