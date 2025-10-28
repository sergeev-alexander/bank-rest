package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.BankSecurityException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;
    private final com.example.bankcards.repository.TransactionRepository transactionRepository;

    @Autowired
    public SecurityServiceImpl(UserRepository userRepository,
                               CardRepository cardRepository,
                               TransferRepository transferRepository,
                               com.example.bankcards.repository.TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.transferRepository = transferRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    public void validateCardOwnership(Long cardId) {
        String currentUserEmail = getCurrentUserEmail();
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card", cardId));

        if (!card.getUser().getEmail().equals(currentUserEmail)) {
            throw new BankSecurityException("You don't own card with id: " + cardId);
        }
    }

    @Override
    public void validateAdminAccess() {
        if (!isAdmin()) {
            throw new BankSecurityException("Admin access required");
        }
    }

    @Override
    public void validateTransferAccess(Long transferId) {
        if (isAdmin()) return;

        Long currentUserId = getCurrentUserId();
        boolean hasAccess = transferRepository.existsByIdAndUserId(transferId, currentUserId);

        if (!hasAccess) {
            throw new BankSecurityException("Access denied to transfer");
        }
    }

    @Override
    public void validateTransactionAccess(Long transactionId) {
        if (isAdmin()) return;

        Long currentUserId = getCurrentUserId();
        boolean hasAccess = transactionRepository.existsByIdAndCardUserId(transactionId, currentUserId);

        if (!hasAccess) {
            throw new BankSecurityException("Access denied to transaction");
        }
    }

    @Override
    public Long getCurrentUserId() {
        String email = getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email:" + email))
                .getId();
    }

    @Override
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BankSecurityException("User not authenticated");
        }

        return authentication.getName();
    }
}