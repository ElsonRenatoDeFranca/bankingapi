package com.example.bankingapi.service;

import com.example.bankingapi.dto.AccountResponse;
import com.example.bankingapi.dto.DepositResponse;
import com.example.bankingapi.dto.EventRequest;
import com.example.bankingapi.dto.TransferResponse;
import com.example.bankingapi.dto.WithdrawResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountService {

    private final Map<String, Integer> accounts = new ConcurrentHashMap<>();

    public void reset() {
        accounts.clear();
    }

    public ResponseEntity<String> getBalance(String accountId) {
        Integer balance = accounts.get(accountId);

        if (balance == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("0");
        }

        return ResponseEntity.ok(String.valueOf(balance));
    }

    public ResponseEntity<?> processEvent(EventRequest request) {
        if (request == null || request.getType() == null) {
            return ResponseEntity.badRequest().build();
        }

        switch (request.getType()) {
            case "deposit":
                return deposit(request.getDestination(), request.getAmount());

            case "withdraw":
                return withdraw(request.getOrigin(), request.getAmount());

            case "transfer":
                return transfer(request.getOrigin(), request.getDestination(), request.getAmount());

            default:
                return ResponseEntity.badRequest().build();
        }
    }

    private ResponseEntity<?> deposit(String destination, Integer amount) {
        if (destination == null || amount == null || amount <= 0) {
            return ResponseEntity.badRequest().build();
        }

        int newBalance = accounts.getOrDefault(destination, 0) + amount;
        accounts.put(destination, newBalance);

        DepositResponse response = new DepositResponse(
                new AccountResponse(destination, newBalance)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private ResponseEntity<?> withdraw(String origin, Integer amount) {
        if (origin == null || amount == null || amount <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Integer currentBalance = accounts.get(origin);

        if (currentBalance == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("0");
        }

        int newBalance = currentBalance - amount;
        accounts.put(origin, newBalance);

        WithdrawResponse response = new WithdrawResponse(
                new AccountResponse(origin, newBalance)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private ResponseEntity<?> transfer(String origin, String destination, Integer amount) {
        if (origin == null || destination == null || amount == null || amount <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Integer originBalance = accounts.get(origin);

        if (originBalance == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("0");
        }

        int newOriginBalance = originBalance - amount;
        int newDestinationBalance = accounts.getOrDefault(destination, 0) + amount;

        accounts.put(origin, newOriginBalance);
        accounts.put(destination, newDestinationBalance);

        TransferResponse response = new TransferResponse(
                new AccountResponse(origin, newOriginBalance),
                new AccountResponse(destination, newDestinationBalance)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}