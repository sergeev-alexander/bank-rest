package com.example.bankcards.util.specifications;

import com.example.bankcards.entity.BlockRequestStatus;
import com.example.bankcards.entity.CardBlock;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class CardBlockSpecifications {

    public static Specification<CardBlock> hasUserId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return null;

            return cb.equal(root.get("user").get("id"), userId);
        };
    }

    public static Specification<CardBlock> hasCardId(Long cardId) {
        return (root, query, cb) -> {
            if (cardId == null) return null;

            return cb.equal(root.get("card").get("id"), cardId);
        };
    }

    public static Specification<CardBlock> hasStatus(BlockRequestStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<CardBlock> requestedAtBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return null;

            if (start != null && end != null && start.isAfter(end)) {
                throw new IllegalArgumentException("Start date must be before end date");
            }

            if (start == null) return cb.lessThanOrEqualTo(root.get("requestedAt"), end);
            if (end == null) return cb.greaterThanOrEqualTo(root.get("requestedAt"), start);

            return cb.between(root.get("requestedAt"), start, end);
        };
    }
}

