package com.example.bankcards.util;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;

public class CardUtils {

    public static CardDTO toDTO(Card card) {
        return new CardDTO(
                card.getId(),
                card.getUser().getId(),
                MaskUtils.maskCardNumber(card.getCardNumber()),
                card.getExpiryDate(),
                card.getBalance(),
                card.getStatus()
        );
    }
}