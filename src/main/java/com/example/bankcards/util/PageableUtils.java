package com.example.bankcards.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PageableUtils {

    private static final Sort.Direction DEFAULT_DIRECTION = Sort.Direction.ASC;

    public static Pageable createPageable(int page, int size, String[] sort) {
        if (sort == null || sort.length == 0) {
            return PageRequest.of(page, size);
        }

        List<Sort.Order> orders = Arrays.stream(sort)
                .filter(s -> s != null && !s.trim().isEmpty())
                .map(PageableUtils::parseSortOrder)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (orders.isEmpty()) {
            return PageRequest.of(page, size);
        }

        return PageRequest.of(page, size, Sort.by(orders));
    }

    private static Sort.Order parseSortOrder(String sortParam) {
        if (sortParam == null || sortParam.trim().isEmpty()) {
            return null;
        }

        String trimmed = sortParam.trim();
        String[] parts = trimmed.split(",", 2);

        try {
            if (parts.length == 1) {
                return new Sort.Order(DEFAULT_DIRECTION, parts[0].trim());
            } else {
                String field = parts[0].trim();
                String directionStr = parts[1].trim();

                if (field.isEmpty()) {
                    return null;
                }

                Sort.Direction direction = directionStr.isEmpty()
                        ? DEFAULT_DIRECTION
                        : Sort.Direction.fromString(directionStr);

                return new Sort.Order(direction, field);
            }
        } catch (IllegalArgumentException ignored) {
            return new Sort.Order(DEFAULT_DIRECTION, parts[0].trim());
        }
    }
}
