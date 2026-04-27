package com.example.bankingapi.controller;

import com.example.bankingapi.dto.EventRequest;
import com.example.bankingapi.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        accountService.reset();
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/balance")
    public ResponseEntity<String> balance(@RequestParam("account_id") String accountId) {
        return accountService.getBalance(accountId);
    }

    @PostMapping("/event")
    public ResponseEntity<?> event(@RequestBody EventRequest request) {
        return accountService.processEvent(request);
    }
}
