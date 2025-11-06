package com.example.bankcards.service;

import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service for managing card transactions.
 * Provides functionality for deposits, withdrawals, and transaction history.
 *
 * @author Bank System Team
 * @since 1.0.0
 */
public interface TransactionService {

    /**
     * Gets a transaction by identifier.
     *
     * @param id transaction identifier
     * @return found transaction
     * @throws com.example.bankcards.exception.NotFoundException if transaction not found
     */
    Transaction getTransactionById(Long id);

    /**
     * Gets all transactions in the system with filtering.
     *
     * @param userId user identifier for filtering (can be null)
     * @param cardId card identifier for filtering (can be null)
     * @param transactionType transaction type for filtering (can be null)
     * @param startDate transaction start date (can be null)
     * @param endDate transaction end date (can be null)
     * @param pageable pagination parameters
     * @return page of all transactions
     */
    Page<Transaction> getAllTransactions(@Nullable Long userId,
                                         @Nullable Long cardId,
                                         @Nullable TransactionType transactionType,
                                         @Nullable LocalDateTime startDate,
                                         @Nullable LocalDateTime endDate,
                                         Pageable pageable);

    /**
     * Gets transactions for a specific user with filtering.
     *
     * @param userId user identifier
     * @param cardId card identifier for filtering (can be null)
     * @param transactionType transaction type for filtering (can be null)
     * @param startDate transaction start date (can be null)
     * @param endDate transaction end date (can be null)
     * @param pageable pagination parameters
     * @return page of user transactions
     */
    Page<Transaction> getUserTransactions(Long userId,
                                         @Nullable Long cardId,
                                         @Nullable TransactionType transactionType,
                                         @Nullable LocalDateTime startDate,
                                         @Nullable LocalDateTime endDate,
                                         Pageable pageable);

    /**
     * Deposits money to a card.
     *
     * @param cardId card identifier
     * @param amount deposit amount
     * @return created deposit transaction
     * @throws com.example.bankcards.exception.NotFoundException if card not found
     * @throws IllegalArgumentException if amount is invalid
     */
    Transaction deposit(Long cardId, BigDecimal amount);

    /**
     * Withdraws money from a card.
     *
     * @param cardId card identifier
     * @param amount withdrawal amount
     * @return created withdrawal transaction
     * @throws com.example.bankcards.exception.NotFoundException if card not found
     * @throws com.example.bankcards.exception.InsufficientFundsException if insufficient funds
     * @throws IllegalArgumentException if amount is invalid
     */
    Transaction withdraw(Long cardId, BigDecimal amount);
}