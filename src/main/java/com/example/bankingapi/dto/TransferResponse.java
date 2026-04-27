package com.example.bankingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class TransferResponse {
    private AccountResponse origin;
    private AccountResponse destination;
}