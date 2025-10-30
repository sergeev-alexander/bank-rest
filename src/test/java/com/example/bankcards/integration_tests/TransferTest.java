package com.example.bankcards.integration_tests;

import com.example.bankcards.TestDataFactory;
import com.example.bankcards.dto.TransferRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class TransferTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateTransferBetweenOwnCards() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        
        Long fromCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "1111222233334444", new BigDecimal("1000.00"));
        Long toCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "5555666677778888", new BigDecimal("500.00"));

        TransferRequest request = TestDataFactory.createTransferRequest(fromCardId, toCardId, new BigDecimal("200.00"));

        mockMvc.perform(post("/api/transfers")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromCardMasked").value("**** **** **** 4444"))
                .andExpect(jsonPath("$.toCardMasked").value("**** **** **** 8888"))
                .andExpect(jsonPath("$.amount").value(200.00))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void shouldDenyTransferFromNonOwnedCard() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        
        Long fromCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, userId, "4444333322221111", new BigDecimal("800.00"));
        Long toCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "9999888877776666", new BigDecimal("300.00"));

        TransferRequest request = TestDataFactory.createTransferRequest(fromCardId, toCardId, new BigDecimal("100.00"));

        mockMvc.perform(post("/api/transfers")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDenyTransferToNonOwnedCard() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);
        
        Long fromCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "1234123412341234", new BigDecimal("600.00"));
        Long toCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, userId, "5555444433332222", new BigDecimal("300.00"));

        TransferRequest request = TestDataFactory.createTransferRequest(fromCardId, toCardId, new BigDecimal("100.00"));

        mockMvc.perform(post("/api/transfers")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetTransferByIdAsOwner() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        
        Long fromCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "6666777788889999", new BigDecimal("1000.00"));
        Long toCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "1111000022223333", new BigDecimal("500.00"));

        Long transferId = TestDataFactory.createTestTransfer(mockMvc, objectMapper, userToken, fromCardId, toCardId, new BigDecimal("150.00"));

        mockMvc.perform(get("/api/transfers/" + transferId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromCardMasked").value("**** **** **** 9999"))
                .andExpect(jsonPath("$.toCardMasked").value("**** **** **** 3333"));
    }

    @Test
    void shouldDenyGetTransferByIdAsNonOwner() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        
        TestDataFactory.UserWithToken otherUser = TestDataFactory.createUserWithLoginAndId(mockMvc, objectMapper, adminToken, "Other", "User", "other@test.com", "password123");
        
        Long fromCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, otherUser.userId, "7777888899990000", new BigDecimal("800.00"));
        Long toCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, otherUser.userId, "2222333344445555", new BigDecimal("500.00"));

        Long transferId = TestDataFactory.createTestTransfer(mockMvc, objectMapper, otherUser.token, fromCardId, toCardId, new BigDecimal("100.00"));

        mockMvc.perform(get("/api/transfers/" + transferId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetUserTransfersAsOwner() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        
        Long fromCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "3333444455556666", new BigDecimal("1000.00"));
        Long toCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "8888999900001111", new BigDecimal("500.00"));

        TestDataFactory.createTestTransfer(mockMvc, objectMapper, userToken, fromCardId, toCardId, new BigDecimal("100.00"));

        mockMvc.perform(get("/api/transfers/user/2")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].fromCardMasked").value("**** **** **** 6666"));
    }

    @Test
    void shouldGetUserTransfersAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        
        Long fromCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "4444555566667777", new BigDecimal("1000.00"));
        Long toCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "9999000011112222", new BigDecimal("500.00"));

        TestDataFactory.createTestTransfer(mockMvc, objectMapper, userToken, fromCardId, toCardId, new BigDecimal("100.00"));

        mockMvc.perform(get("/api/transfers/user/2")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].fromCardMasked").value("**** **** **** 7777"));
    }

    @Test
    void shouldDenyGetUserTransfersForOtherUser() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        Long userId = TestDataFactory.createTestUser(mockMvc, objectMapper, adminToken);

        mockMvc.perform(get("/api/transfers/user/" + userId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetAllTransfersAsAdmin() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        
        Long fromCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "5555666677778888", new BigDecimal("1000.00"));
        Long toCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "1111222233334444", new BigDecimal("500.00"));

        TestDataFactory.createTestTransfer(mockMvc, objectMapper, userToken, fromCardId, toCardId, new BigDecimal("100.00"));

        mockMvc.perform(get("/api/transfers")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].fromCardMasked").value("**** **** **** 8888"));
    }

    @Test
    void shouldDenyGetAllTransfersAsUser() throws Exception {
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);

        mockMvc.perform(get("/api/transfers")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnBadRequestForInvalidTransfer() throws Exception {
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        TransferRequest invalidRequest = TestDataFactory.createInvalidTransferRequest();

        mockMvc.perform(post("/api/transfers")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForInsufficientFunds() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        
        Long fromCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "6666777788889999", new BigDecimal("100.00"));
        Long toCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "2222333344445555", new BigDecimal("500.00"));

        TransferRequest request = TestDataFactory.createTransferRequest(fromCardId, toCardId, new BigDecimal("2000.00"));

        mockMvc.perform(post("/api/transfers")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFilterTransfersByStatus() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        
        Long fromCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "7777888899990000", new BigDecimal("1000.00"));
        Long toCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "3333444455556666", new BigDecimal("500.00"));

        TestDataFactory.createTestTransfer(mockMvc, objectMapper, userToken, fromCardId, toCardId, new BigDecimal("100.00"));

        mockMvc.perform(get("/api/transfers?status=COMPLETED")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].status").value("COMPLETED"));
    }

    @Test
    void shouldFilterTransfersByCardId() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        
        Long fromCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "8888999900001111", new BigDecimal("1000.00"));
        Long toCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "4444555566667777", new BigDecimal("500.00"));

        TestDataFactory.createTestTransfer(mockMvc, objectMapper, userToken, fromCardId, toCardId, new BigDecimal("100.00"));

        mockMvc.perform(get("/api/transfers?cardId=" + fromCardId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].fromCardMasked").value("**** **** **** 1111"));
    }

    @Test
    void shouldHandlePaginationForTransfers() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);
        String userToken = TestDataFactory.getUserToken(mockMvc, objectMapper);
        
        Long fromCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "9999000011112222", new BigDecimal("1000.00"));
        Long toCardId = TestDataFactory.createCardWithUniqueNumber(mockMvc, objectMapper, adminToken, 2L, "5555666677778888", new BigDecimal("500.00"));

        TestDataFactory.createTestTransfer(mockMvc, objectMapper, userToken, fromCardId, toCardId, new BigDecimal("50.00"));
        TestDataFactory.createTestTransfer(mockMvc, objectMapper, userToken, toCardId, fromCardId, new BigDecimal("30.00"));

        mockMvc.perform(get("/api/transfers/user/2?page=0&size=1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void shouldReturnNotFoundForNonExistentTransfer() throws Exception {
        String adminToken = TestDataFactory.getAdminToken(mockMvc, objectMapper);

        mockMvc.perform(get("/api/transfers/999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}