package com.example.bankcards.util;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;

/**
 * Utility class for card-related operations.
 * Provides methods for converting between Card entities and DTOs.
 *
 * @author Bank System Team
 * @since 1.0.0
 */
public class CardUtils {

    /**
     * Converts Card entity to CardDTO with masked card number.
     *
     * @param card card entity to convert
     * @return card DTO with masked card number
     */
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