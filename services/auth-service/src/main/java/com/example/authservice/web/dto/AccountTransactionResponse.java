package com.example.authservice.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class AccountTransactionResponse {

    private final Long id;
    private final String type;
    private final BigDecimal amount;
    private final String currency;
    private final String counterparty;
    private final String reference;
    private final String description;
    private final OffsetDateTime createdAt;
    private final BigDecimal balanceAfter;

    public AccountTransactionResponse(Long id,
                                      String type,
                                      BigDecimal amount,
                                      String currency,
                                      String counterparty,
                                      String reference,
                                      String description,
                                      OffsetDateTime createdAt,
                                      BigDecimal balanceAfter) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.counterparty = counterparty;
        this.reference = reference;
        this.description = description;
        this.createdAt = createdAt;
        this.balanceAfter = balanceAfter;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public String getReference() {
        return reference;
    }

    public String getDescription() {
        return description;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }
}
