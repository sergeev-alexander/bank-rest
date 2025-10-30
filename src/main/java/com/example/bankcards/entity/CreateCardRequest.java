package com.example.bankcards.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCardRequest {

    @NotNull
    private Long userId;

    @NotBlank
    @Size(min = 16, max = 19)
    private String cardNumber;

    @NotNull
    @Future
    private LocalDate expiryDate;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal balance;
}