package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.CreateCardRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service for managing bank cards.
 * Provides functionality for creating, searching, blocking and activating cards.
 *
 * @author Bank System Team
 * @since 1.0.0
 */
public interface CardService {

    /**
     * Finds a card by its identifier.
     *
     * @param id card identifier
     * @return found card
     * @throws com.example.bankcards.exception.NotFoundException if card not found
     */
    Card findById(Long id);

    /**
     * Finds a card by its number.
     *
     * @param cardNumber card number
     * @return found card
     * @throws com.example.bankcards.exception.NotFoundException if card not found
     */
    Card findByCardNumber(String cardNumber);

    /**
     * Gets user cards with filtering.
     *
     * @param userId user identifier
     * @param status card status for filtering (can be null)
     * @param startDate creation start date (can be null)
     * @param endDate creation end date (can be null)
     * @param pageable pagination parameters
     * @return page of user cards
     */
    Page<Card> getUserCardsByUserId(Long userId,
                                    @Nullable CardStatus status,
                                    @Nullable LocalDateTime startDate,
                                    @Nullable LocalDateTime endDate,
                                    Pageable pageable);

    /**
     * Gets user cards (alias for getUserCardsByUserId).
     *
     * @param userId user identifier
     * @param status card status for filtering (can be null)
     * @param startDate creation start date (can be null)
     * @param endDate creation end date (can be null)
     * @param pageable pagination parameters
     * @return page of user cards
     */
    Page<Card> getUserCards(Long userId,
                            @Nullable CardStatus status,
                            @Nullable LocalDateTime startDate,
                            @Nullable LocalDateTime endDate,
                            Pageable pageable);

    /**
     * Gets all cards in the system with filtering.
     *
     * @param userId user identifier for filtering (can be null)
     * @param status card status for filtering (can be null)
     * @param startDate creation start date (can be null)
     * @param endDate creation end date (can be null)
     * @param pageable pagination parameters
     * @return page of all cards
     */
    Page<Card> getAllCards(@Nullable Long userId,
                           @Nullable CardStatus status,
                           @Nullable LocalDateTime startDate,
                           @Nullable LocalDateTime endDate,
                           Pageable pageable);

    /**
     * Gets total user balance across all cards.
     *
     * @param userId user identifier
     * @return total user balance
     */
    BigDecimal getUserBalance(Long userId);

    /**
     * Gets all cards with specific status.
     *
     * @param cardStatus card status
     * @param pageable pagination parameters
     * @return page of cards with specified status
     */
    Page<Card> getAllCardsByStatus(CardStatus cardStatus, Pageable pageable);

    /**
     * Creates a new bank card.
     *
     * @param request card creation request
     * @return created card
     * @throws IllegalArgumentException if card with this number already exists
     * @throws com.example.bankcards.exception.NotFoundException if user not found
     */
    Card createCard(CreateCardRequest request);

    /**
     * Blocks a card by identifier.
     *
     * @param id card identifier
     * @return blocked card
     * @throws IllegalStateException if card is already blocked
     * @throws com.example.bankcards.exception.NotFoundException if card not found
     */
    Card blockCardById(Long id);

    /**
     * Activates a card by identifier.
     *
     * @param id card identifier
     * @return activated card
     * @throws IllegalStateException if card is already active or expired
     * @throws com.example.bankcards.exception.NotFoundException if card not found
     */
    Card activateCard(Long id);

    /**
     * Deletes a card by identifier.
     *
     * @param id card identifier
     * @throws com.example.bankcards.exception.NotFoundException if card not found
     */
    void deleteById(Long id);

    /**
     * Checks if card exists by identifier.
     *
     * @param cardId card identifier
     * @return true if card exists, false otherwise
     */
    boolean existsById(Long cardId);

    /**
     * Checks if card exists by number.
     *
     * @param cardNumber card number
     * @return true if card exists, false otherwise
     */
    boolean existsByCardNumber(String cardNumber);
}