package com.example.bankcards.service;

public interface SecurityService {

    void validateCardOwnership(Long cardId);

    void validateAdminAccess();

    void validateTransferAccess(Long transferId);

    void validateTransactionAccess(Long transactionId);

    boolean isAdmin();

    Long getCurrentUserId();

    String getCurrentUserEmail();
}