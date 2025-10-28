package com.example.bankcards.util.specifications;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class CardSpecifications {

    public static Specification<Card> hasUserId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return null;

            return cb.equal(root.get("user").get("id"), userId);
        };
    }

    public static Specification<Card> hasStatus(CardStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Card> createdAtBetween(LocalDateTime start, LocalDateTime end) {
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
