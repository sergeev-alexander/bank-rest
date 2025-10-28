package com.example.bankcards.service;

import com.example.bankcards.dto.CardBlockRequest;
import com.example.bankcards.entity.BlockRequestStatus;
import com.example.bankcards.entity.CardBlock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

public interface CardBlockService {

    CardBlock createBlockRequest(CardBlockRequest request);

    Page<CardBlock> getBlockRequestsByUserId(Long userId,
                                             @Nullable Long cardId,
                                             @Nullable BlockRequestStatus status,
                                             @Nullable LocalDateTime startDate,
                                             @Nullable LocalDateTime endDate,
                                             Pageable pageable);

    Page<CardBlock> getAllBlockRequests(@Nullable Long userId,
                                        @Nullable Long cardId,
                                        @Nullable BlockRequestStatus status,
                                        @Nullable LocalDateTime startDate,
                                        @Nullable LocalDateTime endDate,
                                        Pageable pageable);

    CardBlock approveBlockRequest(Long blockId);
}