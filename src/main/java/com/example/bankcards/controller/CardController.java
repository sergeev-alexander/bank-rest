package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.CreateCardRequest;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.SecurityService;
import com.example.bankcards.util.CardUtils;
import com.example.bankcards.util.PageableUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/cards")
public class CardController {

    private final SecurityService securityService;
    private final CardService cardService;

    @Autowired
    public CardController(SecurityService securityService,
                          CardService cardService) {
        this.securityService = securityService;
        this.cardService = cardService;
    }

    @GetMapping("/id/{id}")
    public CardDTO getCardById(@PathVariable Long id) {
        if (!securityService.isAdmin()) {
            securityService.validateCardOwnership(id);
        }

        return CardUtils.toDTO(cardService.findById(id));
    }

    @GetMapping("/number/{cardNumber}")
    public CardDTO getCardByNumber(@PathVariable String cardNumber) {
        Card card = cardService.findByCardNumber(cardNumber);

        if (!securityService.isAdmin()) {
            securityService.validateCardOwnership(card.getId());
        }

        return CardUtils.toDTO(card);
    }

    @GetMapping
    public Page<CardDTO> getAllCards(@RequestParam(required = false) Long userId,
                                     @RequestParam(required = false) CardStatus status,
                                     @RequestParam(required = false)
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                     @RequestParam(required = false)
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) String[] sort) {
        securityService.validateAdminAccess();
        Pageable pageable = PageableUtils.createPageable(page, size, sort);

        Page<Card> cardPage = cardService.getAllCards(userId, status, startDate, endDate, pageable);
        return cardPage.map(CardUtils::toDTO);
    }

    @GetMapping("/my-cards")
    public Page<CardDTO> getUserCards(@RequestParam(required = false) CardStatus status,
                                      @RequestParam(required = false)
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                      @RequestParam(required = false)
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(required = false) String[] sort) {

        Long currentUserId = securityService.getCurrentUserId();
        Pageable pageable = PageableUtils.createPageable(page, size, sort);

        Page<Card> cards = cardService.getUserCards(currentUserId, status, startDate, endDate, pageable);
        return cards.map(CardUtils::toDTO);
    }

    @GetMapping("/my-balance")
    public BigDecimal getUserBalance() {
        Long currentUserId = securityService.getCurrentUserId();

        return cardService.getUserBalance(currentUserId);
    }

    @GetMapping("/balance/{userId}")
    public BigDecimal getUserBalanceById(@PathVariable Long userId) {
        securityService.validateAdminAccess();

        return cardService.getUserBalance(userId);
    }

    @GetMapping("/{userId}")
    public Page<CardDTO> getUserCardsByUserId(@PathVariable Long userId,
                                              @RequestParam(required = false) CardStatus status,
                                              @RequestParam(required = false)
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                              @RequestParam(required = false)
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String[] sort) {
        securityService.validateAdminAccess();

        Pageable pageable = PageableUtils.createPageable(page, size, sort);
        Page<Card> cardPage = cardService.getUserCardsByUserId(userId, status, startDate, endDate, pageable);

        return cardPage.map(CardUtils::toDTO);
    }

    @PostMapping
    public CardDTO createCard(@RequestBody @Valid CreateCardRequest request) {
        securityService.validateAdminAccess();
        return CardUtils.toDTO(cardService.createCard(request));
    }

    @PostMapping("/{id}/admin-block")
    public CardDTO blockCardById(@PathVariable Long id) {
        securityService.validateAdminAccess();
        return CardUtils.toDTO(cardService.blockCardById(id));
    }

    @PostMapping("/{id}/activate")
    public CardDTO activateCardById(@PathVariable Long id) {
        securityService.validateAdminAccess();
        return CardUtils.toDTO(cardService.activateCard(id));
    }

    @DeleteMapping("/{id}")
    public void deleteCardById(@PathVariable Long id) {
        securityService.validateAdminAccess();
        cardService.deleteById(id);
    }
}