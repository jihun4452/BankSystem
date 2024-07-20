package helthtest.helth.service;

import helthtest.helth.domain.AccountEntity;
import helthtest.helth.domain.TransactionEntity;
import helthtest.helth.domain.repository.AccountRepository;
import helthtest.helth.domain.repository.TransactionRepository;
import helthtest.helth.dto.transaction.DepositDto;
import helthtest.helth.exception.CustomException;
import helthtest.helth.security.TokenProvider;
import helthtest.helth.type.ErrorCode;
import helthtest.helth.type.Transaction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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


}
