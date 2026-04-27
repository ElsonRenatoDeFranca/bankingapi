package com.example.bankingapi.service;

import com.example.bankingapi.dto.DepositResponse;
import com.example.bankingapi.dto.EventRequest;
import com.example.bankingapi.dto.TransferResponse;
import com.example.bankingapi.dto.WithdrawResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    void setup() {
        accountService = new AccountService();
        accountService.reset();
    }

    @Test
    void shouldReturn404ForNonExistingBalance() {
        ResponseEntity<String> response = accountService.getBalance("1234");

        assertEquals(404, response.getStatusCode().value());
        assertEquals("0", response.getBody());
    }

    @Test
    void shouldCreateAccountWithInitialBalance() {
        ResponseEntity<?> response =
                accountService.processEvent(new EventRequest("deposit", null, "100", 10));

        assertEquals(201, response.getStatusCode().value());

        DepositResponse body = (DepositResponse) response.getBody();

        assertNotNull(body);
        assertEquals("100", body.getDestination().getId());
        assertEquals(10, body.getDestination().getBalance());
    }

    @Test
    void shouldDepositIntoExistingAccount() {
        accountService.processEvent(new EventRequest("deposit", null, "100", 10));

        ResponseEntity<?> response =
                accountService.processEvent(new EventRequest("deposit", null, "100", 10));

        DepositResponse body = (DepositResponse) response.getBody();

        assertNotNull(body);
        assertEquals(20, body.getDestination().getBalance());
    }

    @Test
    void shouldReturnBalanceForExistingAccount() {
        accountService.processEvent(new EventRequest("deposit", null, "100", 10));

        ResponseEntity<String> response = accountService.getBalance("100");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("10", response.getBody());
    }

    @ParameterizedTest
    @ValueSource(strings = {"withdraw", "transfer"})
    void shouldReturn404WhenOriginAccountDoesNotExist(String eventType) {
        EventRequest request = "withdraw".equals(eventType)
                ? new EventRequest(eventType, "200", null, 10)
                : new EventRequest(eventType, "200", "300", 10);

        ResponseEntity<?> response = accountService.processEvent(request);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("0", response.getBody());
    }

    @Test
    void shouldWithdrawFromExistingAccount() {
        accountService.processEvent(new EventRequest("deposit", null, "100", 10));

        ResponseEntity<?> response =
                accountService.processEvent(new EventRequest("withdraw", "100", null, 5));

        WithdrawResponse body = (WithdrawResponse) response.getBody();

        assertNotNull(body);
        assertEquals("100", body.getOrigin().getId());
        assertEquals(5, body.getOrigin().getBalance());
    }

    @Test
    void shouldTransferFromExistingAccount() {
        accountService.processEvent(new EventRequest("deposit", null, "100", 15));

        ResponseEntity<?> response =
                accountService.processEvent(new EventRequest("transfer", "100", "300", 15));

        TransferResponse body = (TransferResponse) response.getBody();

        assertNotNull(body);

        assertEquals("100", body.getOrigin().getId());
        assertEquals(0, body.getOrigin().getBalance());

        assertEquals("300", body.getDestination().getId());
        assertEquals(15, body.getDestination().getBalance());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"invalid", "", "abc"})
    void shouldReturnBadRequestForInvalidEventType(String eventType) {
        EventRequest request = new EventRequest(eventType, "100", "200", 10);

        ResponseEntity<?> response = accountService.processEvent(request);

        assertEquals(400, response.getStatusCode().value());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1, -10})
    void shouldReturnBadRequestForInvalidDepositAmount(Integer amount) {
        EventRequest request = new EventRequest("deposit", null, "100", amount);

        ResponseEntity<?> response = accountService.processEvent(request);

        assertEquals(400, response.getStatusCode().value());
    }
}