package com.example.authservice.web.dto;

import java.math.BigDecimal;

public class AccountSummaryResponse {

    private final boolean provisioned;
    private final String accountNumber;
    private final String sortCode;
    private final String currency;
    private final BigDecimal balance;
    private final BigDecimal overdraftLimit;
    private final BigDecimal availableBalance;

    public AccountSummaryResponse(boolean provisioned,
                                  String accountNumber,
                                  String sortCode,
                                  String currency,
                                  BigDecimal balance,
                                  BigDecimal overdraftLimit,
                                  BigDecimal availableBalance) {
        this.provisioned = provisioned;
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.currency = currency;
        this.balance = balance;
        this.overdraftLimit = overdraftLimit;
        this.availableBalance = availableBalance;
    }

    public static AccountSummaryResponse notProvisioned() {
        return new AccountSummaryResponse(false, null, null, "GBP", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public boolean isProvisioned() {
        return provisioned;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }
}
