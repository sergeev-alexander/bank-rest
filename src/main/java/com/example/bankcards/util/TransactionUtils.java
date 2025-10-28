package com.example.bankcards.util;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Transaction;

public class TransactionUtils {

    public static TransactionDTO toDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setCardId(transaction.getCard().getId());
        dto.setCardMasked(MaskUtils.maskCardNumber(transaction.getCard().getCardNumber()));
        dto.setTransactionType(transaction.getTransactionType());
        dto.setAmount(transaction.getAmount());
        dto.setCreatedAt(transaction.getCreatedAt());
        return dto;
    }
}

