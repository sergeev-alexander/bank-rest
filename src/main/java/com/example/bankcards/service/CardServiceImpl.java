package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.CreateCardRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.specifications.CardSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Card findById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card", id));
    }

    @Override
    public Card findByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new NotFoundException("Card not found with number: " + cardNumber));
    }

    @Override
    public Page<Card> getUserCardsByUserId(Long userId,
                                           @Nullable CardStatus status,
                                           @Nullable LocalDateTime startDate,
                                           @Nullable LocalDateTime endDate,
                                           Pageable pageable) {
        Specification<Card> spec = Specification
                .where(CardSpecifications.hasUserId(userId))
                .and(CardSpecifications.hasStatus(status))
                .and(CardSpecifications.createdAtBetween(startDate, endDate));

        return cardRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Card> getUserCards(Long userId,
                                    @Nullable CardStatus status,
                                    @Nullable LocalDateTime startDate,
                                    @Nullable LocalDateTime endDate,
                                    Pageable pageable) {
        return getUserCardsByUserId(userId, status, startDate, endDate, pageable);
    }

    @Override
    public BigDecimal getUserBalance(Long userId) {
        return cardRepository.sumBalanceByUserId(userId)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public Page<Card> getAllCards(@Nullable Long userId,
                                  @Nullable CardStatus status,
                                  @Nullable LocalDateTime startDate,
                                  @Nullable LocalDateTime endDate,
                                  Pageable pageable) {
        Specification<Card> spec = Specification
                .where(CardSpecifications.hasUserId(userId))
                .and(CardSpecifications.hasStatus(status))
                .and(CardSpecifications.createdAtBetween(startDate, endDate));

        return cardRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Card> getAllCardsByStatus(CardStatus status, Pageable pageable) {
        return cardRepository.findByStatus(status, pageable);
    }

    @Override
    public Card createCard(CreateCardRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User", request.getUserId()));

        if (existsByCardNumber(request.getCardNumber())) {
            throw new IllegalArgumentException("Card with this number already exists");
        }

        Card card = new Card();
        card.setUser(user);
        card.setCardNumber(request.getCardNumber());
        card.setExpiryDate(request.getExpiryDate());
        card.setBalance(request.getBalance());
        card.setStatus(CardStatus.ACTIVE);

        return cardRepository.save(card);
    }

    @Override
    public Card blockCardById(Long id) {
        Card card = findById(id);

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new IllegalStateException("Card is already blocked");
        }

        card.setStatus(CardStatus.BLOCKED);

        return cardRepository.save(card);
    }

    @Override
    public Card activateCard(Long id) {
        Card card = findById(id);

        if (card.getStatus() == CardStatus.ACTIVE) {
            throw new IllegalStateException("Card is already active");
        }

        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot activate expired card");
        }

        card.setStatus(CardStatus.ACTIVE);

        return cardRepository.save(card);
    }

    @Override
    public void deleteById(Long id) {
        if (!existsById(id)) {
            throw new NotFoundException("Card", id);
        }

        cardRepository.deleteById(id);
    }

    @Override
    public boolean existsByCardNumber(String cardNumber) {
        return cardRepository.existsByCardNumber(cardNumber);
    }

    @Override
    public boolean existsById(Long cardId) {
        return cardRepository.existsById(cardId);
    }
}