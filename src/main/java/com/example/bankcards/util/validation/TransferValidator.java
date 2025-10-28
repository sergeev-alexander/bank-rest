package com.example.bankcards.util.validation;

import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transfer;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class TransferValidator implements ConstraintValidator<ValidTransfer, Transfer> {

    @Override
    public boolean isValid(Transfer transfer, ConstraintValidatorContext context) {
        if (transfer == null) {
            return true;
        }

        boolean isValid = true;

        if (transfer.getFromCard().getId().equals(transfer.getToCard().getId())) {
            addConstraintViolation(context, "Cannot transfer to the same card");
            isValid = false;
        }

        if (transfer.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            addConstraintViolation(context, "Transfer amount must be positive");
            isValid = false;
        }

        if (transfer.getFromCard().getStatus() != CardStatus.ACTIVE) {
            addConstraintViolation(context, "Source card is not active");
            isValid = false;
        }
        if (transfer.getToCard().getStatus() != CardStatus.ACTIVE) {
            addConstraintViolation(context, "Target card is not active");
            isValid = false;
        }

        if (transfer.getFromCard().getBalance().compareTo(transfer.getAmount()) < 0) {
            addConstraintViolation(context, "Insufficient funds on source card");
            isValid = false;
        }

        return isValid;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}