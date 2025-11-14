package com.example.authservice.web.dto;

import java.math.BigDecimal;
import java.util.List;

public class StatementSummaryResponse {

    private final String month;
    private final BigDecimal openingBalance;
    private final BigDecimal closingBalance;
    private final BigDecimal inflow;
    private final BigDecimal outflow;
    private final List<AccountTransactionResponse> transactions;

    public StatementSummaryResponse(String month,
                                    BigDecimal openingBalance,
                                    BigDecimal closingBalance,
                                    BigDecimal inflow,
                                    BigDecimal outflow,
                                    List<AccountTransactionResponse> transactions) {
        this.month = month;
        this.openingBalance = openingBalance;
        this.closingBalance = closingBalance;
        this.inflow = inflow;
        this.outflow = outflow;
        this.transactions = transactions;
    }

    public String getMonth() {
        return month;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public BigDecimal getInflow() {
        return inflow;
    }

    public BigDecimal getOutflow() {
        return outflow;
    }

    public List<AccountTransactionResponse> getTransactions() {
        return transactions;
    }
}
