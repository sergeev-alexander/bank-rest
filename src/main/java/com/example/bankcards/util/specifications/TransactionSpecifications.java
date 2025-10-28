package com.example.bankcards.util.specifications;

import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TransactionSpecifications {

    public static Specification<Transaction> hasUserId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return null;

            return cb.equal(root.get("card").get("user").get("id"), userId);
        };
    }

    public static Specification<Transaction> hasCardId(Long cardId) {
        return (root, query, cb) -> {
            if (cardId == null) return null;

            return cb.equal(root.get("card").get("id"), cardId);
        };
    }

    public static Specification<Transaction> hasTransactionType(TransactionType transactionType) {
        return (root, query, cb) -> {
            if (transactionType == null) return null;

            return cb.equal(root.get("transactionType"), transactionType);
        };
    }

    public static Specification<Transaction> createdAtBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return null;

            if (start != null && end != null && start.isAfter(end)) {
                throw new IllegalArgumentException("Start date must be before end date");
            }

            if (start == null) return cb.lessThanOrEqualTo(root.get("createdAt"), end);
            if (end == null) return cb.greaterThanOrEqualTo(root.get("createdAt"), start);

            return cb.between(root.get("createdAt"), start, end);
        };
    }
}

