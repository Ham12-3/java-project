package com.example.authservice.web;

import com.example.authservice.service.CustomerAccountService;
import com.example.authservice.web.dto.AccountSummaryResponse;
import com.example.authservice.web.dto.AccountTransactionResponse;
import com.example.authservice.web.dto.MoneyMovementRequest;
import com.example.authservice.web.dto.StatementSummaryResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.time.YearMonth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app/customers/{customerId}")
public class AppAccountController {

    private final CustomerAccountService accountService;

    public AppAccountController(CustomerAccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/account")
    public AccountSummaryResponse summary(@PathVariable Long customerId) {
        return accountService.getSummary(customerId);
    }

    @GetMapping("/transactions")
    public List<AccountTransactionResponse> transactions(@PathVariable Long customerId) {
        return accountService.getTransactions(customerId);
    }

    @PostMapping("/send")
    public AccountSummaryResponse send(@PathVariable Long customerId,
                                       @Valid @RequestBody MoneyMovementRequest request) {
        return accountService.send(customerId, request);
    }

    @PostMapping("/receive")
    public AccountSummaryResponse receive(@PathVariable Long customerId,
                                          @Valid @RequestBody MoneyMovementRequest request) {
        return accountService.receive(customerId, request);
    }

    @GetMapping("/statements")
    public StatementSummaryResponse statement(@PathVariable Long customerId,
                                              @RequestParam(required = false) String month) {
        YearMonth target = month != null ? YearMonth.parse(month) : YearMonth.now();
        return accountService.getStatement(customerId, target);
    }
}
