package com.example.bankcards.dto;

import com.example.bankcards.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class TransactionDTO {

    private Long id;
    private Long cardId;
    private String cardMasked;
    private TransactionType transactionType;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}

