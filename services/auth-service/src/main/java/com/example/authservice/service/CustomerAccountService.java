package com.example.authservice.service;

import com.example.authservice.domain.AccountTransaction;
import com.example.authservice.domain.AccountTransactionRepository;
import com.example.authservice.domain.Customer;
import com.example.authservice.domain.CustomerAccount;
import com.example.authservice.domain.CustomerAccountRepository;
import com.example.authservice.domain.TransactionType;
import com.example.authservice.web.dto.AccountSummaryResponse;
import com.example.authservice.web.dto.AccountTransactionResponse;
import com.example.authservice.web.dto.MoneyMovementRequest;
import com.example.authservice.web.dto.StatementSummaryResponse;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CustomerAccountService {

    private static final BigDecimal DEFAULT_OVERDRAFT = new BigDecimal("500.00");
    private static final Random RANDOM = new Random();

    private final CustomerAccountRepository accounts;
    private final AccountTransactionRepository transactions;

    public CustomerAccountService(CustomerAccountRepository accounts,
                                  AccountTransactionRepository transactions) {
        this.accounts = accounts;
        this.transactions = transactions;
    }

    @Transactional
    public CustomerAccount ensureAccount(Customer customer) {
        return accounts.findByCustomerId(customer.getId())
                .orElseGet(() -> createAccount(customer));
    }

    @Transactional
    public AccountSummaryResponse getSummary(Long customerId) {
        return accounts.findByCustomerId(customerId)
                .map(this::toSummary)
                .orElse(AccountSummaryResponse.notProvisioned());
    }

    @Transactional
    public List<AccountTransactionResponse> getTransactions(Long customerId) {
        return accounts.findByCustomerId(customerId)
                .map(account -> transactions.findByAccountIdOrderByCreatedAtDesc(account.getId()).stream()
                        .map(this::toTransactionResponse)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    @Transactional
    public AccountSummaryResponse send(Long customerId, MoneyMovementRequest request) {
        CustomerAccount account = accounts.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalStateException("Account not provisioned yet"));
        BigDecimal amount = sanitizeAmount(request);
        BigDecimal available = account.getBalance().add(account.getOverdraftLimit());
        if (amount.compareTo(available) > 0) {
            throw new IllegalArgumentException("Insufficient funds including overdraft allowance");
        }
        account.setBalance(account.getBalance().subtract(amount));
        recordTransaction(account, TransactionType.DEBIT, amount.negate(), request);
        return toSummary(account);
    }

    @Transactional
    public AccountSummaryResponse receive(Long customerId, MoneyMovementRequest request) {
        CustomerAccount account = accounts.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalStateException("Account not provisioned yet"));
        BigDecimal amount = sanitizeAmount(request);
        account.setBalance(account.getBalance().add(amount));
        recordTransaction(account, TransactionType.CREDIT, amount, request);
        return toSummary(account);
    }

    private BigDecimal sanitizeAmount(MoneyMovementRequest request) {
        if (request.getAmount() == null) {
            throw new IllegalArgumentException("Amount is required");
        }
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        return request.getAmount().setScale(2, RoundingMode.HALF_UP);
    }

    private void recordTransaction(CustomerAccount account,
                                   TransactionType type,
                                   BigDecimal amount,
                                   MoneyMovementRequest request) {
        AccountTransaction txn = new AccountTransaction();
        txn.setAccount(account);
        txn.setType(type);
        txn.setAmount(amount.abs());
        txn.setCurrency(account.getCurrency());
        txn.setCounterparty(request.getCounterparty());
        txn.setReference(request.getReference());
        txn.setDescription(request.getDescription());
        txn.setBalanceAfter(account.getBalance());
        transactions.save(txn);
    }

    private CustomerAccount createAccount(Customer customer) {
        CustomerAccount account = new CustomerAccount();
        account.setCustomer(customer);
        account.setAccountNumber(generateAccountNumber());
        account.setSortCode(generateSortCode());
        account.setBalance(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        account.setOverdraftLimit(DEFAULT_OVERDRAFT);
        return accounts.save(account);
    }

    private String generateAccountNumber() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            builder.append(RANDOM.nextInt(10));
        }
        return builder.toString();
    }

    private String generateSortCode() {
        int part1 = RANDOM.nextInt(90) + 10;
        int part2 = RANDOM.nextInt(90) + 10;
        int part3 = RANDOM.nextInt(90) + 10;
        return String.format(Locale.UK, "%02d-%02d-%02d", part1, part2, part3);
    }

    private AccountSummaryResponse toSummary(CustomerAccount account) {
        BigDecimal available = account.getBalance().add(account.getOverdraftLimit());
        return new AccountSummaryResponse(
                true,
                account.getAccountNumber(),
                account.getSortCode(),
                account.getCurrency(),
                account.getBalance(),
                account.getOverdraftLimit(),
                available
        );
    }

    private AccountTransactionResponse toTransactionResponse(AccountTransaction txn) {
        return new AccountTransactionResponse(
                txn.getId(),
                txn.getType().name(),
                txn.getAmount(),
                txn.getCurrency(),
                txn.getCounterparty(),
                txn.getReference(),
                txn.getDescription(),
                txn.getCreatedAt(),
                txn.getBalanceAfter()
        );
    }

    public BigDecimal calculateMonthClosingBalance(Long customerId, YearMonth month) {
        return accounts.findByCustomerId(customerId)
                .map(CustomerAccount::getBalance)
                .orElse(BigDecimal.ZERO);
    }

    @Transactional
    public StatementSummaryResponse getStatement(Long customerId, YearMonth month) {
        CustomerAccount account = accounts.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalStateException("Account not provisioned yet"));
        List<AccountTransactionResponse> monthTransactions = transactions
                .findByAccountIdOrderByCreatedAtDesc(account.getId()).stream()
                .filter(txn -> YearMonth.from(txn.getCreatedAt()).equals(month))
                .map(this::toTransactionResponse)
                .collect(Collectors.toList());

        BigDecimal inflow = monthTransactions.stream()
                .filter(txn -> "CREDIT".equals(txn.getType()))
                .map(AccountTransactionResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal outflow = monthTransactions.stream()
                .filter(txn -> "DEBIT".equals(txn.getType()))
                .map(AccountTransactionResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal closing = account.getBalance();
        BigDecimal net = inflow.subtract(outflow);
        BigDecimal opening = closing.subtract(net);

        return new StatementSummaryResponse(month.toString(), opening, closing, inflow, outflow, monthTransactions);
    }
}
