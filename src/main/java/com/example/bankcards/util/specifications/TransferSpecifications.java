package com.example.bankcards.util.specifications;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.TransferStatus;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class TransferSpecifications {

    public static Specification<Transfer> hasUserId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return null;

            Join<Transfer, Card> fromCard = root.join("fromCard");
            Join<Transfer, Card> toCard = root.join("toCard");

            return cb.or(
                    cb.equal(fromCard.get("user").get("id"), userId),
                    cb.equal(toCard.get("user").get("id"), userId)
            );
        };
    }

    public static Specification<Transfer> hasCardId(Long cardId) {
        return (root, query, cb) -> {
            if (cardId == null) return null;

            return cb.or(
                    cb.equal(root.get("fromCard").get("id"), cardId),
                    cb.equal(root.get("toCard").get("id"), cardId)
            );
        };
    }

    public static Specification<Transfer> hasStatus(TransferStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Transfer> createdAtBetween(LocalDateTime start, LocalDateTime end) {
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

    public static Specification<Transfer> amountBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;

            if (min != null && max != null && min.compareTo(max) > 0) {
                throw new IllegalArgumentException("Min amount must be less than max amount");
            }

            if (min == null) return cb.lessThanOrEqualTo(root.get("amount"), max);
            if (max == null) return cb.greaterThanOrEqualTo(root.get("amount"), min);

            return cb.between(root.get("amount"), min, max);
        };
    }
}
