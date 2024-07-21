package helthtest.helth.service;

import helthtest.helth.domain.AccountEntity;
import helthtest.helth.domain.TransactionEntity;
import helthtest.helth.domain.repository.AccountRepository;
import helthtest.helth.domain.repository.TransactionRepository;
import helthtest.helth.dto.transaction.*;
import helthtest.helth.exception.CustomException;
import helthtest.helth.security.TokenProvider;
import helthtest.helth.type.ErrorCode;
import helthtest.helth.type.Transaction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor

public class TransactionService {
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final TokenProvider tokenProvider;
    private final AccountRepository accountRepository;

    //계좌 삭제여부 확인
    @Transactional
    public DepositDto.Response deposit(DepositDto.Request request){
        AccountEntity accountEntity=accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(()-> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));
        if(accountEntity.getIsDeleted()){
            throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        //계좌 있을시 잔액 변경
        accountEntity.setAmount(accountEntity.getAmount()+request.getAmount());

        transactionRepository.save(
                TransactionEntity.builder()
                        .account(accountEntity)
                        .transactionType(Transaction.DEPOSIT)
                        .amount(request.getAmount())
                        .depositName(request.getDepositName())
                        .build()
        );
        return DepositDto.Response.builder()
                .accountNumber(request.getAccountNumber())
                .depositName(request.getDepositName())
                .amount(request.getAmount())
                .transacted_at(LocalDateTime.now())
                .build();
    }

    @Transactional //트래잭션 관리 db
    public WithdrawDto.Response withdraw(String token, WithdrawDto.Request request){
        Long tokenUserId=tokenProvider.getUserIdFromToken(token);
        AccountEntity accountEntity=accountRepository.findByAccountNumber(
                request.getAccountNumber())
                .orElseThrow(()-> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        if(accountEntity.getIsDeleted()){
            throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        if(!Objects.equals(tokenUserId,accountEntity.getUser().getId())){
            throw new CustomException(ErrorCode.TOKEN_NOT_MATCH_USER);
        }

        if(request.getAmount() > accountEntity.getAmount()){
            throw new CustomException(ErrorCode.BALANCE_NOT_ENOUGH);
        }

       // accountEntity.setAmount(accountEntity.getAmount() - request.getAmount());

        transactionRepository.save(
                TransactionEntity.builder()
                        .account(accountEntity)
                        .transactionType(Transaction.WITHDRAW)
                        .amount(request.getAmount())
                        .withdrawName(request.getWithdrawName())
                        .build()
        );

        return WithdrawDto.Response.builder()
                .accountNumber(request.getAccountNumber())
                .withdrawName(request.getWithdrawName())
                .amount(request.getAmount())
                .transacted_at(LocalDateTime.now())
                .build();
    }
    @Transactional
    public RemittanceDto.Response remittance(String token, RemittanceDto.Request request) {
        AccountEntity sentAccountEntity = accountRepository.findByAccountNumber(
                        request.getSentAccountNumber())
                .orElseThrow(() -> new CustomException(ErrorCode.SENT_ACCOUNT_NOT_FOUND));

        AccountEntity recievedAccountEntity = accountRepository.findByAccountNumber(
                        request.getReceivedAccountNumber())
                .orElseThrow(() -> new CustomException(ErrorCode.RECEIVED_ACCOUNT_NOT_FOUND));

        // 토큰의 사용자와 보내는 계좌의 사용자 확인
        Long tokenUserId = tokenProvider.getUserIdFromToken(token);

        if (!Objects.equals(tokenUserId, sentAccountEntity.getUser().getId())) {
            throw new CustomException(ErrorCode.TOKEN_NOT_MATCH_USER);
        }

        // 보내는 계좌와 받는 계좌 존재/삭제 여부 확인
        if (sentAccountEntity.getIsDeleted()) {
            throw new CustomException(ErrorCode.SENT_ACCOUNT_NOT_FOUND);
        } else if (recievedAccountEntity.getIsDeleted()) {
            throw new CustomException(ErrorCode.RECEIVED_ACCOUNT_NOT_FOUND);
        }

        // (송금 요청 금액 > 보내는 계좌의 잔액)의 경우 예외 발생
        if (request.getAmount() > sentAccountEntity.getAmount()) {
            throw new CustomException(ErrorCode.BALANCE_NOT_ENOUGH);
        }
        // 보내는 계좌, 받는 계좌의 잔액 변경
        sentAccountEntity.setAmount(sentAccountEntity.getAmount() - request.getAmount());
        recievedAccountEntity.setAmount(recievedAccountEntity.getAmount() + request.getAmount());

        // 거래 테이블에 거래 저장
        transactionRepository.save(
                TransactionEntity.builder()
                        .account(sentAccountEntity)
                        .transactionType(Transaction.REMITTANCE)
                        .amount(request.getAmount())
                        .receivedName(recievedAccountEntity.getUser().getUsername())
                        .receivedAccount(request.getReceivedAccountNumber())
                        .build()
        );

        return RemittanceDto.Response.builder()
                .sentAccountNumber(request.getSentAccountNumber())
                .receivedAccountNumber(request.getReceivedAccountNumber())
                .receivedBank(recievedAccountEntity.getBank())
                .receivedName(recievedAccountEntity.getUser().getUsername())
                .amount(request.getAmount())
                .build();
    }

    @Transactional
    public TransactionListDto.Response getTransactionList
    (
            String token, TransactionListDto.Request request
    ) {
        AccountEntity accountEntity = getValidAccountEntity(request.getAccountNumber());

        if (!Objects.equals(tokenProvider.getUserIdFromToken(token), accountEntity.getUser().getId())) {
            throw new CustomException(ErrorCode.TOKEN_NOT_MATCH_USER);
        }

        TransactionEntity transactionEntity = transactionRepository.findById(request.getAccountId())
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));
        if (!request.getAccountNumber().equals(transactionEntity.getAccount().getAccountNumber())) {
            throw new CustomException(ErrorCode.NOT_EQUAL_ID_AND_ACCOUNT_NUMBER);
        }

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        LocalDate nowDate = LocalDate.now();
        int defaultDateRange = 7;
        int maxDateRange = 7;

        if (startDate == null && endDate == null) {
            LocalDate weekAgoDate = nowDate.minusDays(defaultDateRange - 1);

            return getTransactionListResponse(request.getAccountId(), weekAgoDate, nowDate);
        }

        if (startDate == null || endDate == null) {
            throw new CustomException(ErrorCode.INVALID_DATE);
        }

        if (startDate.isAfter(endDate)) {
            throw new CustomException(ErrorCode.INVALID_DATE);
        }

        if (endDate.isAfter(nowDate)) {
            throw new CustomException(ErrorCode.INVALID_DATE);
        }

        int betweenDays = (int) ChronoUnit.DAYS.between(startDate, endDate);
        if (betweenDays + 1 > maxDateRange) {
            throw new CustomException(ErrorCode.INVALID_DATE_RANGE);
        }

        return getTransactionListResponse(request.getAccountId(), startDate, endDate);
    }

    private AccountEntity getValidAccountEntity(String accountNumber) {
        AccountEntity accountEntity = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (accountEntity.getIsDeleted()) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        return accountEntity;
    }

    private String getTransactionTargetName(TransactionEntity transaction) {
        switch (transaction.getTransactionType()) {
            case WITHDRAW -> {
                return transaction.getWithdrawName();
            }
            case DEPOSIT -> {
                return transaction.getDepositName();
            }
            case REMITTANCE -> {
                return transaction.getReceivedName();
            }
        }
        return ErrorCode.TRANSACTION_TYPE_NOT_FOUND.getDescription();
    }

    private TransactionListDto.Response getTransactionListResponse(
            Long accountId, LocalDate startDate, LocalDate endDate
    ) {
        List<TransactionEntity> resultList = transactionRepository.findByAccountIdAndTransactedAtBetween(
                accountId, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)
        );
        return TransactionListDto.Response.builder()
                .transactionList(resultList.stream()
                        .map(transaction ->
                                TransactionDto.builder()
                                        .id(transaction.getId())
                                        .transactionTargetName(getTransactionTargetName(transaction))
                                        .amount(transaction.getAmount())
                                        .type(transaction.getTransactionType())
                                        .transactedAt(transaction.getTransactedAt())
                                        .build()
                        ).toList())
                .build();
    }

}
