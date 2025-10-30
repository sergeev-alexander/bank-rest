package com.example.bankcards.integration_tests;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    static PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("test_db")
                .withUsername("test_user")
                .withPassword("test_password")
                .withReuse(true);
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanupDatabase() {
        jdbcTemplate.execute("DELETE FROM transactions");
        jdbcTemplate.execute("DELETE FROM transfers");
        jdbcTemplate.execute("DELETE FROM card_blocks");
        jdbcTemplate.execute("DELETE FROM cards");
        jdbcTemplate.execute("DELETE FROM users WHERE role != 'ROLE_ADMIN'");
        
        jdbcTemplate.execute("ALTER SEQUENCE users_id_seq RESTART WITH 2");
        jdbcTemplate.execute("ALTER SEQUENCE cards_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE transfers_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE card_blocks_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE transactions_id_seq RESTART WITH 1");
    }
}