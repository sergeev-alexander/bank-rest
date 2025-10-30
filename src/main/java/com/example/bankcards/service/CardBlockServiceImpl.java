package com.example.bankcards.service;

import com.example.bankcards.dto.CardBlockRequest;
import com.example.bankcards.entity.BlockRequestStatus;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlock;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardBlockRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.specifications.CardBlockSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CardBlockServiceImpl implements CardBlockService {

    private final CardBlockRepository cardBlockRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Autowired
    public CardBlockServiceImpl(CardBlockRepository cardBlockRepository,
                                CardRepository cardRepository,
                                UserRepository userRepository) {
        this.cardBlockRepository = cardBlockRepository;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CardBlock createBlockRequest(CardBlockRequest request) {
        Card card = cardRepository.findById(request.getCardId())
                .orElseThrow(() -> new NotFoundException("Card", request.getCardId()));
        User user = userRepository.findById(card.getUser().getId())
                .orElseThrow(() -> new NotFoundException("User", card.getUser().getId()));

        boolean hasAnyRequest = cardBlockRepository.existsByCardId(request.getCardId());

        if (hasAnyRequest) {
            throw new IllegalStateException("Card already has block request");
        }

        CardBlock cardBlock = new CardBlock();
        cardBlock.setCard(card);
        cardBlock.setUser(user);

        return cardBlockRepository.save(cardBlock);
    }

    @Override
    public Page<CardBlock> getBlockRequestsByUserId(Long userId,
                                                    @Nullable Long cardId,
                                                    @Nullable BlockRequestStatus status,
                                                    @Nullable LocalDateTime startDate,
                                                    @Nullable LocalDateTime endDate,
                                                    Pageable pageable) {
        Specification<CardBlock> spec = Specification
                .where(CardBlockSpecifications.hasUserId(userId))
                .and(CardBlockSpecifications.hasCardId(cardId))
                .and(CardBlockSpecifications.hasStatus(status))
                .and(CardBlockSpecifications.requestedAtBetween(startDate, endDate));

        return cardBlockRepository.findAll(spec, pageable);
    }

    @Override
    public Page<CardBlock> getAllBlockRequests(@Nullable Long userId,
                                               @Nullable Long cardId,
                                               @Nullable BlockRequestStatus status,
                                               @Nullable LocalDateTime startDate,
                                               @Nullable LocalDateTime endDate,
                                               Pageable pageable) {
        Specification<CardBlock> spec = Specification
                .where(CardBlockSpecifications.hasUserId(userId))
                .and(CardBlockSpecifications.hasCardId(cardId))
                .and(CardBlockSpecifications.hasStatus(status))
                .and(CardBlockSpecifications.requestedAtBetween(startDate, endDate));

        return cardBlockRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public CardBlock approveBlockRequest(Long blockId) {
        CardBlock cardBlock = cardBlockRepository.findById(blockId)
                .orElseThrow(() -> new NotFoundException("CardBlock", blockId));

        if (cardBlock.getStatus() != BlockRequestStatus.PENDING) {
            throw new IllegalStateException("Block request already processed");
        }

        Card card = cardBlock.getCard();
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);

        cardBlock.setStatus(BlockRequestStatus.APPROVED);

        return cardBlockRepository.save(cardBlock);
    }
}