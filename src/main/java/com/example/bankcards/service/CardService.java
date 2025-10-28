package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.CreateCardRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CardService {

    Card findById(Long id);

    Card findByCardNumber(String cardNumber);

    Page<Card> getUserCardsByUserId(Long userId,
                                    @Nullable CardStatus status,
                                    @Nullable LocalDateTime startDate,
                                    @Nullable LocalDateTime endDate,
                                    Pageable pageable);

    Page<Card> getUserCards(Long userId,
                            @Nullable CardStatus status,
                            @Nullable LocalDateTime startDate,
                            @Nullable LocalDateTime endDate,
                            Pageable pageable);

    Page<Card> getAllCards(@Nullable Long userId,
                           @Nullable CardStatus status,
                           @Nullable LocalDateTime startDate,
                           @Nullable LocalDateTime endDate,
                           Pageable pageable);

    BigDecimal getUserBalance(Long userId);

    Page<Card> getAllCardsByStatus(CardStatus cardStatus, Pageable pageable);

    Card createCard(CreateCardRequest request);

    Card blockCardById(Long id);

    Card activateCard(Long id);

    void deleteById(Long id);

    boolean existsById(Long cardId);

    boolean existsByCardNumber(String cardNumber);
}