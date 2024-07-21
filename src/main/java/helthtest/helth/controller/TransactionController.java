package helthtest.helth.controller;

import helthtest.helth.domain.repository.TransactionRepository;
import helthtest.helth.dto.transaction.DepositDto;
import helthtest.helth.dto.transaction.RemittanceDto;
import helthtest.helth.dto.transaction.TransactionListDto;
import helthtest.helth.dto.transaction.WithdrawDto;
import helthtest.helth.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    private static final String TOKEN_PREFIX = "Bearer ";

    @PostMapping("/deposit")
    public ResponseEntity<DepositDto.Response> deposit(
            @RequestBody @Valid DepositDto.Request request
    ) {
        DepositDto.Response response = transactionService.deposit(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/withdraw")
    public ResponseEntity<WithdrawDto.Response> withdraw(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody @Valid WithdrawDto.Request request
    ) {
        WithdrawDto.Response response = transactionService.withdraw(
                token.substring(TOKEN_PREFIX.length()), request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/remittance")
    public ResponseEntity<RemittanceDto.Response> remittance(
            @RequestHeader(name="Authorization") String token,
            @RequestBody @Valid RemittanceDto.Request request
    ) {
        return ResponseEntity.ok(
                transactionService.remittance(token.substring(TOKEN_PREFIX.length()), request)
        );
    }

    @GetMapping("/transaction-list")
    public ResponseEntity<TransactionListDto.Response> getTransactionList(
            @RequestHeader(name="Authorization") String token,
            @RequestBody @Valid TransactionListDto.Request request
    ) {
        return ResponseEntity.ok(
                transactionService.getTransactionList(token.substring(TOKEN_PREFIX.length()), request)
        );
    }
}





