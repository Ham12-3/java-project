package com.example.authservice.web;

import com.example.authservice.service.MfaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AuthControllerIntegrationTest {

    @Container
    static final OracleContainer ORACLE = new OracleContainer(DockerImageName.parse("gvenzl/oracle-xe:21-slim"))
            .withUsername("testuser")
            .withPassword("testpass")
            .withDatabaseName("FREEPDB1");

    @DynamicPropertySource
    static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", ORACLE::getJdbcUrl);
        registry.add("spring.datasource.username", ORACLE::getUsername);
        registry.add("spring.datasource.password", ORACLE::getPassword);
        registry.add("spring.datasource.driver-class-name", ORACLE::getDriverClassName);
        registry.add("spring.liquibase.enabled", () -> true);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MfaService mfaService;

    @Test
    void registerAndLoginWithMfa() throws Exception {
        String registerPayload = "{" +
                "\"username\":\"integration-user\"," +
                "\"email\":\"integration.user@example.com\"," +
                "\"password\":\"VerySecurePassword1!\"," +
                "\"mfaEnabled\":true" +
                "}";

        String registerResponse = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerPayload))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode registerJson = objectMapper.readTree(registerResponse);
        assertThat(registerJson.get("username").asText()).isEqualTo("integration-user");
        assertThat(registerJson.get("mfaEnabled").asBoolean()).isTrue();
        String secret = registerJson.get("mfaSecret").asText();
        assertThat(secret).isNotBlank();

        String mfaCode = mfaService.currentCode(secret);
        String loginPayload = "{" +
                "\"username\":\"integration-user\"," +
                "\"password\":\"VerySecurePassword1!\"," +
                "\"mfaCode\":\"" + mfaCode + "\"" +
                "}";

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode loginJson = objectMapper.readTree(loginResponse);
        assertThat(loginJson.get("token").asText()).isNotBlank();
    }
}
