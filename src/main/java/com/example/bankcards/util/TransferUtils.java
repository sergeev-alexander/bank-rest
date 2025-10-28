package com.example.bankcards.util;

import com.example.bankcards.dto.TransferDTO;
import com.example.bankcards.entity.Transfer;

public class TransferUtils {

    public static TransferDTO toDTO(Transfer transfer) {
        return new TransferDTO(
                transfer.getId(),
                MaskUtils.maskCardNumber(transfer.getFromCard().getCardNumber()),
                MaskUtils.maskCardNumber(transfer.getToCard().getCardNumber()),
                transfer.getAmount(),
                transfer.getStatus(),
                transfer.getCreatedAt()
        );
    }
}