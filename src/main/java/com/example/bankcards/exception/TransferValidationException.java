package com.example.bankcards.exception;

public class TransferValidationException extends BankCardsException {

    public TransferValidationException(String message) {
        super(message);
    }
}