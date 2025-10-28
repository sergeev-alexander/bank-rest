package com.example.bankcards.controller;

import com.example.bankcards.dto.CardBlockDTO;
import com.example.bankcards.dto.CardBlockRequest;
import com.example.bankcards.entity.BlockRequestStatus;
import com.example.bankcards.entity.CardBlock;
import com.example.bankcards.exception.BankSecurityException;
import com.example.bankcards.service.CardBlockService;
import com.example.bankcards.service.SecurityService;
import com.example.bankcards.util.CardBlockUtils;
import com.example.bankcards.util.PageableUtils;
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
@RequestMapping("/api/card-blocks")
public class CardBlockController {

    private final SecurityService securityService;
    private final CardBlockService cardBlockService;

    @Autowired
    public CardBlockController(SecurityService securityService, CardBlockService cardBlockService) {
        this.securityService = securityService;
        this.cardBlockService = cardBlockService;
    }

    @PostMapping
    public CardBlockDTO createBlockRequest(@RequestBody @Valid CardBlockRequest request) {
        securityService.validateCardOwnership(request.getCardId());

        return CardBlockUtils.toDTO(cardBlockService.createBlockRequest(request));
    }

    @GetMapping("/user/{userId}")
    public Page<CardBlockDTO> getBlockRequestsByUserId(@PathVariable Long userId,
                                                       @RequestParam(required = false) Long cardId,
                                                       @RequestParam(required = false) BlockRequestStatus status,
                                                       @RequestParam(required = false)
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                       @RequestParam(required = false)
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(required = false) String[] sort) {
        Long currentUserId = securityService.getCurrentUserId();

        if (!securityService.isAdmin() && !currentUserId.equals(userId)) {
            throw new BankSecurityException("Can only view own block requests");
        }

        Pageable pageable = PageableUtils.createPageable(page, size, sort);

        Page<CardBlock> cardBlocks = cardBlockService.getBlockRequestsByUserId(userId, cardId, status, startDate, endDate, pageable);

        return cardBlocks.map(CardBlockUtils::toDTO);
    }

    @GetMapping
    public Page<CardBlockDTO> getAllBlockRequests(@RequestParam(required = false) Long userId,
                                                  @RequestParam(required = false) Long cardId,
                                                  @RequestParam(required = false) BlockRequestStatus status,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false) String[] sort) {

        securityService.validateAdminAccess();

        Pageable pageable = PageableUtils.createPageable(page, size, sort);

        Page<CardBlock> cardBlocks = cardBlockService.getAllBlockRequests(userId, cardId, status, startDate, endDate, pageable);

        return cardBlocks.map(CardBlockUtils::toDTO);
    }

    @PostMapping("/{blockId}/approve")
    public CardBlockDTO approveBlockRequest(@PathVariable Long blockId) {
        securityService.validateAdminAccess();

        CardBlock cardBlock = cardBlockService.approveBlockRequest(blockId);
        return CardBlockUtils.toDTO(cardBlock);
    }
}