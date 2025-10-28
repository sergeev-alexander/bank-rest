package com.example.bankcards.repository;

import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    Page<Transaction> findByCardId(Long cardId, Pageable pageable);

    Page<Transaction> findByCardUserId(Long userId, Pageable pageable);

    Page<Transaction> findByCardIdAndTransactionType(Long cardId, TransactionType type, Pageable pageable);

    List<Transaction> findByCardId(Long cardId);

    boolean existsByIdAndCardUserId(Long transactionId, Long userId);
}

