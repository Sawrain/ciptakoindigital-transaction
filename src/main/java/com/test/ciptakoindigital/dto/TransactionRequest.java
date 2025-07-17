package com.test.ciptakoindigital.dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private Double amount;
    private String description;
}

