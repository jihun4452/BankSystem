package helthtest.helth.dto.account;

import helthtest.helth.type.Bank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class OtherBankAccountCreateDto {
    @Getter
    @Builder
    public static class Request{
        @NotNull(message="은행명은 필수입니다.")
        private Bank bank;

        @NotBlank(message = "계좌명은 필수입니다.")
        @Size(min=2,max=8, message="계좌명은 2-8자리입니다.")
        private String accountName;

        @NotBlank(message = "계좌번호는 필수입니다.")
        @Size(min=11,max=13,message = "계좌번호는 11-13입니다.")
        private String accountNumber;

        @NotNull
        @Min(0)
        private Long initialBalance;
    }

    @Getter
    @Builder
    public static class Response{
        private Bank bank;

        private String accountNumber;

        private Long amount;

        private LocalDateTime createdAt;
    }
}
