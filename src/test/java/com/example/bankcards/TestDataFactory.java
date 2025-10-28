package com.example.bankcards;

import com.example.bankcards.dto.CardBlockRequest;
import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.dto.RegistrationRequest;
import com.example.bankcards.dto.TransactionRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.CreateCardRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.TransactionType;
import com.example.bankcards.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestDataFactory {

    public static RegistrationRequest createRegistrationRequest() {
        return new RegistrationRequest(
                "John",
                "Doe",
                "john@test.com",
                "password123");
    }

    public static CreateUserRequest createUserRequest() {
        return new CreateUserRequest(
                "Jane",
                "Smith",
                "jane@test.com",
                "password123",
                Role.ROLE_USER);
    }

    public static CreateCardRequest createCardRequest(Long userId) {
        return new CreateCardRequest(
                userId,
                "1234567890123456",
                LocalDate.now().plusYears(2),
                new BigDecimal("1000.00"));
    }

    public static User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@test.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.ROLE_USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    public static String getAdminToken(MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        LoginRequest adminLogin = new LoginRequest("admin@bank.com", "admin123");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    public static String getUserToken(MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        RegistrationRequest regRequest = createRegistrationRequest();
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isOk());

        LoginRequest userLogin = new LoginRequest("john@test.com", "password123");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLogin)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    public static Long createTestUser(MockMvc mockMvc, ObjectMapper objectMapper, String adminToken) throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest(
                "Test", "User", "testuser@test.com", "password123", Role.ROLE_USER);

        MvcResult result = mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    public static Long createTestCard(MockMvc mockMvc, ObjectMapper objectMapper, String adminToken, Long userId) throws Exception {
        CreateCardRequest cardRequest = new CreateCardRequest(
                userId, "1234567890123456", LocalDate.now().plusYears(2), new BigDecimal("1000.00"));

        MvcResult result = mockMvc.perform(post("/api/cards")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    public static CreateCardRequest createCardRequestWithBalance(Long userId, String cardNumber, BigDecimal balance) {
        return new CreateCardRequest(
                userId,
                cardNumber,
                LocalDate.now().plusYears(3),
                balance);
    }

    public static CreateCardRequest createExpiredCardRequest(Long userId) {
        return new CreateCardRequest(
                userId,
                "9876543210987654",
                LocalDate.now().minusYears(1),
                new BigDecimal("500.00"));
    }

    public static CreateCardRequest createInvalidCardRequest() {
        return new CreateCardRequest(
                null,
                "invalid",
                null,
                new BigDecimal("-100"));
    }

    public static CardBlockRequest createCardBlockRequest(Long cardId) {
        return new CardBlockRequest(cardId);
    }

    public static CardBlockRequest createInvalidCardBlockRequest() {
        return new CardBlockRequest(null);
    }

    public static Long createTestCardBlock(MockMvc mockMvc, ObjectMapper objectMapper, String userToken, Long cardId) throws Exception {
        CardBlockRequest blockRequest = createCardBlockRequest(cardId);

        MvcResult result = mockMvc.perform(post("/api/card-blocks")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blockRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    public static TransactionRequest createTransactionRequest(BigDecimal amount, TransactionType type) {
        return new TransactionRequest(amount, type);
    }

    public static TransactionRequest createInvalidTransactionRequest() {
        return new TransactionRequest(new BigDecimal("-100"), TransactionType.DEPOSIT);
    }

    public static Long createTestDeposit(MockMvc mockMvc, ObjectMapper objectMapper, String adminToken, Long cardId, BigDecimal amount) throws Exception {
        TransactionRequest request = createTransactionRequest(amount, TransactionType.DEPOSIT);

        MvcResult result = mockMvc.perform(post("/api/transactions/deposit/" + cardId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    public static Long createTestWithdraw(MockMvc mockMvc, ObjectMapper objectMapper, String adminToken, Long cardId, BigDecimal amount) throws Exception {
        TransactionRequest request = createTransactionRequest(amount, TransactionType.WITHDRAW);

        MvcResult result = mockMvc.perform(post("/api/transactions/withdraw/" + cardId)
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    public static TransferRequest createTransferRequest(Long fromCardId, Long toCardId, BigDecimal amount) {
        return new TransferRequest(fromCardId, toCardId, amount);
    }

    public static TransferRequest createInvalidTransferRequest() {
        return new TransferRequest(null, null, new BigDecimal("-100"));
    }

    public static Long createTestTransfer(MockMvc mockMvc, ObjectMapper objectMapper, String userToken, Long fromCardId, Long toCardId, BigDecimal amount) throws Exception {
        TransferRequest request = createTransferRequest(fromCardId, toCardId, amount);

        MvcResult result = mockMvc.perform(post("/api/transfers")
                        .with(csrf())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    public static String createUserWithLogin(MockMvc mockMvc, ObjectMapper objectMapper, String adminToken, String firstName, String lastName, String email, String password) throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest(firstName, lastName, email, password, Role.ROLE_USER);
        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest(email, password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    public static UserWithToken createUserWithLoginAndId(MockMvc mockMvc, ObjectMapper objectMapper, String adminToken, String firstName, String lastName, String email, String password) throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest(firstName, lastName, email, password, Role.ROLE_USER);
        MvcResult createResult = mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andReturn();
        
        Long userId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        LoginRequest loginRequest = new LoginRequest(email, password);
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("token").asText();
        return new UserWithToken(userId, token);
    }

    public static class UserWithToken {
        public final Long userId;
        public final String token;
        
        public UserWithToken(Long userId, String token) {
            this.userId = userId;
            this.token = token;
        }
    }

    public static Long createCardWithUniqueNumber(MockMvc mockMvc, ObjectMapper objectMapper, String adminToken, Long userId, String cardNumber, BigDecimal balance) throws Exception {
        CreateCardRequest cardRequest = createCardRequestWithBalance(userId, cardNumber, balance);
        MvcResult result = mockMvc.perform(post("/api/cards")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }
}