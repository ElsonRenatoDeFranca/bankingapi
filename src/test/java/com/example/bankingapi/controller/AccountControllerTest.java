package com.example.bankingapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void resetState() throws Exception {
        mockMvc.perform(post("/reset"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    void shouldReturn404ForNonExistingAccountBalance() throws Exception {
        mockMvc.perform(get("/balance")
                        .param("account_id", "1234"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("0"));
    }

    @Test
    void shouldCreateAccountWithInitialBalance() throws Exception {
        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "type":"deposit",
                              "destination":"100",
                              "amount":10
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destination.id").value("100"))
                .andExpect(jsonPath("$.destination.balance").value(10));
    }

    @Test
    void shouldDepositIntoExistingAccount() throws Exception {
        createDeposit("100", 10);

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "type":"deposit",
                              "destination":"100",
                              "amount":10
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destination.id").value("100"))
                .andExpect(jsonPath("$.destination.balance").value(20));
    }

    @Test
    void shouldGetBalanceForExistingAccount() throws Exception {
        createDeposit("100", 10);

        mockMvc.perform(get("/balance")
                        .param("account_id", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"withdraw", "transfer"})
    void shouldReturn404WhenOriginAccountDoesNotExist(String eventType) throws Exception {
        String payload = "withdraw".equals(eventType)
                ? """
                  {
                    "type":"withdraw",
                    "origin":"200",
                    "amount":10
                  }
                  """
                : """
                  {
                    "type":"transfer",
                    "origin":"200",
                    "amount":15,
                    "destination":"300"
                  }
                  """;

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(content().string("0"));
    }

    @Test
    void shouldWithdrawFromExistingAccount() throws Exception {
        createDeposit("100", 10);

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "type":"withdraw",
                              "origin":"100",
                              "amount":5
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.origin.id").value("100"))
                .andExpect(jsonPath("$.origin.balance").value(5));
    }

    @Test
    void shouldTransferFromExistingAccount() throws Exception {
        createDeposit("100", 15);

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "type":"transfer",
                              "origin":"100",
                              "amount":15,
                              "destination":"300"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.origin.id").value("100"))
                .andExpect(jsonPath("$.origin.balance").value(0))
                .andExpect(jsonPath("$.destination.id").value("300"))
                .andExpect(jsonPath("$.destination.balance").value(15));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", ""})
    void shouldReturnBadRequestForInvalidEventType(String eventType) throws Exception {
        String payload = """
                {
                  "type":"%s",
                  "origin":"100",
                  "amount":10
                }
                """.formatted(eventType);

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void shouldReturnBadRequestForInvalidDepositAmount(int amount) throws Exception {
        String payload = """
                {
                  "type":"deposit",
                  "destination":"100",
                  "amount":%d
                }
                """.formatted(amount);

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    private void createDeposit(String destination, int amount) throws Exception {
        String payload = """
                {
                  "type":"deposit",
                  "destination":"%s",
                  "amount":%d
                }
                """.formatted(destination, amount);

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());
    }
}