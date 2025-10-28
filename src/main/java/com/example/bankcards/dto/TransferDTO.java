package com.example.bankcards.dto;

import com.example.bankcards.entity.TransferStatus;
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
public class TransferDTO {

    private Long id;
    private String fromCardMasked;
    private String toCardMasked;
    private BigDecimal amount;
    private TransferStatus status;
    private LocalDateTime createdAt;
}