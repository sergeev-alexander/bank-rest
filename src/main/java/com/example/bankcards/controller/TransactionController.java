package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.dto.TransactionRequest;
import com.example.bankcards.entity.TransactionType;
import com.example.bankcards.service.SecurityService;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.util.PageableUtils;
import com.example.bankcards.util.TransactionUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final SecurityService securityService;
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(SecurityService securityService,
                                 TransactionService transactionService) {
        this.securityService = securityService;
        this.transactionService = transactionService;
    }

    @GetMapping("/{transactionId}")
    public TransactionDTO getTransactionById(@PathVariable Long transactionId) {
        if (!securityService.isAdmin()) {
            securityService.validateTransactionAccess(transactionId);
        }
        return TransactionUtils.toDTO(transactionService.getTransactionById(transactionId));
    }

    @GetMapping("/my-transactions")
    public Page<TransactionDTO> getMyTransactions(@RequestParam(required = false) Long cardId,
                                                  @RequestParam(required = false) TransactionType transactionType,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false) String[] sort) {

        Long currentUserId = securityService.getCurrentUserId();
        Pageable pageable = PageableUtils.createPageable(page, size, sort);

        return transactionService.getUserTransactions(currentUserId, cardId, transactionType, startDate, endDate, pageable)
                .map(TransactionUtils::toDTO);
    }

    @GetMapping
    public Page<TransactionDTO> getAllTransactions(@RequestParam(required = false) Long userId,
                                                   @RequestParam(required = false) Long cardId,
                                                   @RequestParam(required = false) TransactionType transactionType,
                                                   @RequestParam(required = false)
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                   @RequestParam(required = false)
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(required = false) String[] sort) {

        securityService.validateAdminAccess();
        Pageable pageable = PageableUtils.createPageable(page, size, sort);

        return transactionService.getAllTransactions(userId, cardId, transactionType, startDate, endDate, pageable)
                .map(TransactionUtils::toDTO);
    }

    @PostMapping("/deposit/{cardId}")
    public TransactionDTO deposit(@PathVariable Long cardId,
                                  @RequestBody @Valid TransactionRequest request) {
        securityService.validateAdminAccess();
        return TransactionUtils.toDTO(transactionService.deposit(cardId, request.getAmount()));
    }

    @PostMapping("/withdraw/{cardId}")
    public TransactionDTO withdraw(@PathVariable Long cardId,
                                   @RequestBody @Valid TransactionRequest request) {
        securityService.validateAdminAccess();
        return TransactionUtils.toDTO(transactionService.withdraw(cardId, request.getAmount()));
    }
}