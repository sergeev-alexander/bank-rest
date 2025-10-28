package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferDTO;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.TransferStatus;
import com.example.bankcards.exception.BankSecurityException;
import com.example.bankcards.service.SecurityService;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.util.PageableUtils;
import com.example.bankcards.util.TransferUtils;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final SecurityService securityService;
    private final TransferService transferService;

    @Autowired
    public TransferController(SecurityService securityService, TransferService transferService) {
        this.securityService = securityService;
        this.transferService = transferService;
    }

    @GetMapping("/{transferId}")
    public TransferDTO getTransferById(@PathVariable Long transferId) {
        securityService.validateTransferAccess(transferId);
        return TransferUtils.toDTO(transferService.getTransferById(transferId));
    }

    @GetMapping("/user/{userId}")
    public Page<TransferDTO> getUserTransfers(@PathVariable Long userId,
                                              @RequestParam(required = false) Long cardId,
                                              @RequestParam(required = false) TransferStatus status,
                                              @RequestParam(required = false)
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                              @RequestParam(required = false)
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size,
                                              @RequestParam(required = false) String[] sort) {
        Long currentUserId = securityService.getCurrentUserId();
        if (!securityService.isAdmin() && !currentUserId.equals(userId)) {
            throw new BankSecurityException("Can only view own transfers");
        }

        Pageable pageable = PageableUtils.createPageable(page, size, sort);

        return transferService.getTransfersByUserId(userId, cardId, status, startDate, endDate, pageable)
                .map(TransferUtils::toDTO);
    }

    @GetMapping
    public Page<TransferDTO> getAllTransfers(@RequestParam(required = false) Long userId,
                                             @RequestParam(required = false) Long cardId,
                                             @RequestParam(required = false) TransferStatus status,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                             @RequestParam(required = false) BigDecimal minAmount,
                                             @RequestParam(required = false) BigDecimal maxAmount,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String[] sort) {

        securityService.validateAdminAccess();

        Pageable pageable = PageableUtils.createPageable(page, size, sort);

        return transferService.getAllTransfers(userId, cardId, status, startDate, endDate, minAmount, maxAmount, pageable)
                .map(TransferUtils::toDTO);
    }

    @PostMapping
    public TransferDTO createTransfer(@RequestBody @Valid TransferRequest request) {
        securityService.validateCardOwnership(request.getFromCardId());
        securityService.validateCardOwnership(request.getToCardId());

        return TransferUtils.toDTO(transferService.createTransfer(request));
    }
}