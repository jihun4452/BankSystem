package helthtest.helth.dto.transaction;

import helthtest.helth.type.Bank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

public class RemittanceDto {
    @Getter
    public static class Request{
        @NotBlank(message = "보내는 계좌번호는 필수입니다.")
        private String sentAccountNumber;
        @NotBlank(message = "받는 계좌번호는 필수입니다.")
        private String receivedAccountNumber;
        @NotBlank(message = "송금액은 필수값입니다.")
        @Min(value = 1,message = "송금 최소금액은 1원입니다.")
        private Long amount;

    }
    @Getter
    @Builder

    public static class Response{
        private String sentAccountNumber;
        private String receivedAccountNumber;
        private Bank receivedBank;
        private String receivedName;
        private Long amount;
    }
}
