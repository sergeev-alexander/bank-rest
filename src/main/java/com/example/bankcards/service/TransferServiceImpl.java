package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.TransferStatus;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.util.specifications.TransferSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransferServiceImpl implements TransferService {

    public final TransferRepository transferRepository;
    public final CardRepository cardRepository;

    @Autowired
    public TransferServiceImpl(TransferRepository transferRepository,
                               CardRepository cardRepository) {
        this.transferRepository = transferRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    public Transfer getTransferById(Long id) {
        return transferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transfer", id));
    }

    @Override
    @Transactional
    public Transfer createTransfer(TransferRequest request) {
        Card fromCard = cardRepository.findById(request.getFromCardId())
                .orElseThrow(() -> new NotFoundException("Card", request.getFromCardId()));
        Card toCard = cardRepository.findById(request.getToCardId())
                .orElseThrow(() -> new NotFoundException("Card", request.getToCardId()));

        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        Transfer transfer = new Transfer(fromCard, toCard, request.getAmount(), TransferStatus.PENDING);

        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        transfer.setStatus(TransferStatus.COMPLETED);

        return transferRepository.save(transfer);
    }

    @Override
    public Page<Transfer> getTransfersByUserId(Long userId,
                                               @Nullable Long cardId,
                                               @Nullable TransferStatus status,
                                               @Nullable LocalDateTime startDate,
                                               @Nullable LocalDateTime endDate,
                                               Pageable pageable) {
        Specification<Transfer> spec = Specification
                .where(TransferSpecifications.hasUserId(userId))
                .and(TransferSpecifications.hasCardId(cardId))
                .and(TransferSpecifications.hasStatus(status))
                .and(TransferSpecifications.createdAtBetween(startDate, endDate));

        return transferRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Transfer> getAllTransfers(@Nullable Long userId,
                                          @Nullable Long cardId,
                                          @Nullable TransferStatus status,
                                          @Nullable LocalDateTime startDate,
                                          @Nullable LocalDateTime endDate,
                                          @Nullable BigDecimal minAmount,
                                          @Nullable BigDecimal maxAmount,
                                          Pageable pageable) {
        Specification<Transfer> spec = Specification
                .where(TransferSpecifications.hasUserId(userId))
                .and(TransferSpecifications.hasCardId(cardId))
                .and(TransferSpecifications.hasStatus(status))
                .and(TransferSpecifications.createdAtBetween(startDate, endDate))
                .and(TransferSpecifications.amountBetween(minAmount, maxAmount));

        return transferRepository.findAll(spec, pageable);
    }
}