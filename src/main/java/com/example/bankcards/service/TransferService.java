package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.TransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service for managing money transfers between cards.
 * Provides functionality for creating and retrieving transfers.
 *
 * @author Bank System Team
 * @since 1.0.0
 */
public interface TransferService {

    /**
     * Gets a transfer by identifier.
     *
     * @param id transfer identifier
     * @return found transfer
     * @throws com.example.bankcards.exception.NotFoundException if transfer not found
     */
    Transfer getTransferById(Long id);

    /**
     * Gets transfers for a specific user with filtering.
     *
     * @param userId user identifier
     * @param cardId card identifier for filtering (can be null)
     * @param status transfer status for filtering (can be null)
     * @param startDate transfer start date (can be null)
     * @param endDate transfer end date (can be null)
     * @param pageable pagination parameters
     * @return page of user transfers
     */
    Page<Transfer> getTransfersByUserId(Long userId,
                                        @Nullable Long cardId,
                                        @Nullable TransferStatus status,
                                        @Nullable LocalDateTime startDate,
                                        @Nullable LocalDateTime endDate,
                                        Pageable pageable);

    /**
     * Gets all transfers in the system with filtering.
     *
     * @param userId user identifier for filtering (can be null)
     * @param cardId card identifier for filtering (can be null)
     * @param status transfer status for filtering (can be null)
     * @param startDate transfer start date (can be null)
     * @param endDate transfer end date (can be null)
     * @param minAmount minimum transfer amount (can be null)
     * @param maxAmount maximum transfer amount (can be null)
     * @param pageable pagination parameters
     * @return page of all transfers
     */
    Page<Transfer> getAllTransfers(@Nullable Long userId,
                                   @Nullable Long cardId,
                                   @Nullable TransferStatus status,
                                   @Nullable LocalDateTime startDate,
                                   @Nullable LocalDateTime endDate,
                                   @Nullable BigDecimal minAmount,
                                   @Nullable BigDecimal maxAmount,
                                   Pageable pageable);

    /**
     * Creates a new money transfer between cards.
     *
     * @param request transfer creation request
     * @return created transfer
     * @throws com.example.bankcards.exception.NotFoundException if source or destination card not found
     * @throws com.example.bankcards.exception.InsufficientFundsException if insufficient funds
     * @throws com.example.bankcards.exception.TransferValidationException if transfer validation fails
     */
    Transfer createTransfer(TransferRequest request);
}