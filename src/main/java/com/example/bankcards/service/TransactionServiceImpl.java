package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionType;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.util.specifications.TransactionSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                 CardRepository cardRepository) {
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction", id));
    }

    @Override
    public Page<Transaction> getAllTransactions(@Nullable Long userId,
                                               @Nullable Long cardId,
                                               @Nullable TransactionType transactionType,
                                               @Nullable LocalDateTime startDate,
                                               @Nullable LocalDateTime endDate,
                                               Pageable pageable) {
        Specification<Transaction> spec = Specification
                .where(TransactionSpecifications.hasUserId(userId))
                .and(TransactionSpecifications.hasCardId(cardId))
                .and(TransactionSpecifications.hasTransactionType(transactionType))
                .and(TransactionSpecifications.createdAtBetween(startDate, endDate));

        return transactionRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Transaction> getUserTransactions(Long userId,
                                                 @Nullable Long cardId,
                                                 @Nullable TransactionType transactionType,
                                                 @Nullable LocalDateTime startDate,
                                                 @Nullable LocalDateTime endDate,
                                                 Pageable pageable) {
        Specification<Transaction> spec = Specification
                .where(TransactionSpecifications.hasUserId(userId))
                .and(TransactionSpecifications.hasCardId(cardId))
                .and(TransactionSpecifications.hasTransactionType(transactionType))
                .and(TransactionSpecifications.createdAtBetween(startDate, endDate));

        return transactionRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public Transaction deposit(Long cardId, BigDecimal amount) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card", cardId));

        card.setBalance(card.getBalance().add(amount));
        cardRepository.save(card);

        Transaction transaction = new Transaction(card, TransactionType.DEPOSIT, amount);
        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public Transaction withdraw(Long cardId, BigDecimal amount) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card", cardId));

        if (card.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        }

        card.setBalance(card.getBalance().subtract(amount));
        cardRepository.save(card);

        Transaction transaction = new Transaction(card, TransactionType.WITHDRAW, amount);
        return transactionRepository.save(transaction);
    }
}

