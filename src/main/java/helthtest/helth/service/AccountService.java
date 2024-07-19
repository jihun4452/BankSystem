package helthtest.helth.service;

import helthtest.helth.domain.AccountEntity;
import helthtest.helth.domain.UserEntity;
import helthtest.helth.domain.repository.AccountRepository;
import helthtest.helth.domain.repository.UserRepository;
import helthtest.helth.dto.AccountCreateDto;
import helthtest.helth.dto.AccountDeleteRequest;
import helthtest.helth.dto.account.OtherBankAccountCreateDto;
import helthtest.helth.exception.CustomException;
import helthtest.helth.security.TokenProvider;
import helthtest.helth.type.Bank;
import helthtest.helth.type.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;


    @Transactional
    public AccountCreateDto.Response createAccount(String token, AccountCreateDto.Request request) {
        // 토큰에서 추출한 사용자와 요청으로 받은 사용자가 동일한지 비교
        Long tokenUserId = tokenProvider.getUserIdFromToken(token);
        validateTokenUser(tokenUserId, request.getUserId());

        UserEntity user = userRepository.findById(tokenUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        AccountEntity account = createAndSaveAccount(request, user);

        // 저장된 정보를 DTO 로 반환 후 컨트롤러로 넘김
        return toAccountResponse(account);
    }

    private void validateTokenUser(Long tokenUserId, Long requestUserId) {
        if (!Objects.equals(requestUserId, tokenUserId)) {
            throw new CustomException(ErrorCode.TOKEN_NOT_MATCH_USER);
        }
    }

    private AccountEntity createAndSaveAccount(AccountCreateDto.Request request, UserEntity user) {
        String newAccountNumber = generateAccountNumber();
        String accountName = request.getAccountName() == null ? newAccountNumber : request.getAccountName();

        return accountRepository.save(
                AccountEntity.builder()
                        .user(user)
                        .bank(Bank.SSun)
                        .accountNumber(newAccountNumber)
                        .accountName(accountName)
                        .amount(request.getInitialBalance())
                        .isDeleted(false)
                        .build()
        );
    }

    private AccountCreateDto.Response toAccountResponse(AccountEntity accountEntity) {
        return AccountCreateDto.Response.builder()
                .userId(accountEntity.getUser().getId())
                .accountNumber(accountEntity.getAccountNumber())
                .amount(accountEntity.getAmount())
                .createdAt(accountEntity.getCreatedAt())
                .build();
    }

    private String generateAccountNumber() {
        // 계좌번호 11~13자리 생성(초기값: 893-0000-0000)
        // 계좌테이블 가장 마지막으로 생성된 계좌번호 +1 한 숫자 생성
        return accountRepository.findFirstByOrderByIdDesc()
                .map(accountEntity -> String.valueOf(Long.parseLong(accountEntity.getAccountNumber()) + 1))
                .orElse("89300000000");
    }

    @Transactional
    public String deleteAccount(String token, AccountDeleteRequest request) {
        AccountEntity accountEntity = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateDeleteRequest(token, accountEntity);

        AccountEntity updatedAccountEntity = accountEntity
                .withIsDeleted(true)
                .withDeletedAt(LocalDateTime.now());

        accountRepository.save(updatedAccountEntity);

        return accountEntity.getAccountNumber() + " 계좌가 삭제되었습니다.";
    }

    private void validateDeleteRequest(String token, AccountEntity accountEntity) {
        Long tokenUserId = tokenProvider.getUserIdFromToken(token);

        if (!Objects.equals(tokenUserId, accountEntity.getUser().getId())) {
            throw new CustomException(ErrorCode.USER_NOT_PERMITTED);
        }

        // 계좌의 잔액이 0원인지 확인, 0원이 아니면 예외발생
        if (accountEntity.getAmount() != 0) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_EMPTY);
        }
    }

    @Transactional
    public OtherBankAccountCreateDto.Response registerOtherBankAccount(String token,
                                                                       OtherBankAccountCreateDto.Request request) {
        Long tokenUserId = tokenProvider.getUserIdFromToken(token);
        UserEntity user = userRepository.findById(tokenUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        validateBankAndAccountNumber(request);

        AccountEntity accountEntity = createAndSaveOtherBankAccount(request, user);

        return toOtherBankAccountResponse(accountEntity);
    }

    private void validateBankAndAccountNumber(OtherBankAccountCreateDto.Request request) {
        // 등록할 은행이 SSun 은행이 아닌지 검증
        if (Bank.SSun.equals(request.getBank())) {
            throw new CustomException(ErrorCode.ONLY_CAN_REGISTERED_OTHER_BANK);
        }

        // 계좌 번호 중복 여부 확인
        if (accountRepository.findByAccountNumber(request.getAccountNumber()).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_ACCOUNT_NUMBER);
        }
    }

    private AccountEntity createAndSaveOtherBankAccount(OtherBankAccountCreateDto.Request request, UserEntity user) {
        return accountRepository.save(
                AccountEntity.builder()
                        .user(user)
                        .bank(request.getBank())
                        .accountNumber(request.getAccountNumber())
                        .accountName(request.getAccountName())
                        .amount(request.getInitialBalance())
                        .isDeleted(false)
                        .build()
        );
    }

    private OtherBankAccountCreateDto.Response toOtherBankAccountResponse(AccountEntity accountEntity) {
        return OtherBankAccountCreateDto.Response.builder()
                .bank(accountEntity.getBank())
                .accountNumber(accountEntity.getAccountNumber())
                .amount(accountEntity.getAmount())
                .createdAt(accountEntity.getCreatedAt())
                .build();
    }
}
