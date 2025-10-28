package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.TransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransferService {

    Transfer getTransferById(Long id);

    Page<Transfer> getTransfersByUserId(Long userId,
                                        @Nullable Long cardId,
                                        @Nullable TransferStatus status,
                                        @Nullable LocalDateTime startDate,
                                        @Nullable LocalDateTime endDate,
                                        Pageable pageable);

    Page<Transfer> getAllTransfers(@Nullable Long userId,
                                   @Nullable Long cardId,
                                   @Nullable TransferStatus status,
                                   @Nullable LocalDateTime startDate,
                                   @Nullable LocalDateTime endDate,
                                   @Nullable BigDecimal minAmount,
                                   @Nullable BigDecimal maxAmount,
                                   Pageable pageable);

    Transfer createTransfer(TransferRequest request);
}