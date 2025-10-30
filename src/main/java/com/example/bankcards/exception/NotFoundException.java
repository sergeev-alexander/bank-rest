package com.example.bankcards.exception;

public class NotFoundException extends BankCardsException {

    public NotFoundException(String resource, Long id) {
        super(resource + " not found with id: " + id);
    }

    public NotFoundException(String message) {
        super(message);
    }
}