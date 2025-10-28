package com.example.bankcards.dto;

import com.example.bankcards.entity.BlockRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CardBlockDTO {

    private Long id;
    private Long cardId;
    private String maskedCardNumber;
    private Long userId;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private BlockRequestStatus status;
}
