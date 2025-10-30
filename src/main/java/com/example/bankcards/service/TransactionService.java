package com.example.bankcards.service;

import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionService {

    Transaction getTransactionById(Long id);

    Page<Transaction> getAllTransactions(@Nullable Long userId,
                                         @Nullable Long cardId,
                                         @Nullable TransactionType transactionType,
                                         @Nullable LocalDateTime startDate,
                                         @Nullable LocalDateTime endDate,
                                         Pageable pageable);

    Page<Transaction> getUserTransactions(Long userId,
                                         @Nullable Long cardId,
                                         @Nullable TransactionType transactionType,
                                         @Nullable LocalDateTime startDate,
                                         @Nullable LocalDateTime endDate,
                                         Pageable pageable);

    Transaction deposit(Long cardId, BigDecimal amount);

    Transaction withdraw(Long cardId, BigDecimal amount);
}