package com.example.bankcards.util;

import com.example.bankcards.dto.CardBlockDTO;
import com.example.bankcards.entity.CardBlock;
import org.springframework.stereotype.Component;

@Component
public class CardBlockUtils {

    private CardBlockUtils() {
        // empty
    }

    public static CardBlockDTO toDTO(CardBlock cardBlock) {
        return new CardBlockDTO(
                cardBlock.getId(),
                cardBlock.getCard().getId(),
                MaskUtils.maskCardNumber(cardBlock.getCard().getCardNumber()),
                cardBlock.getUser().getId(),
                cardBlock.getRequestedAt(),
                cardBlock.getProcessedAt(),
                cardBlock.getStatus()
        );
    }
}
