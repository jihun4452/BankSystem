package helthtest.helth.dto.transaction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class DepositDto {
    @Getter
    public static class Request{
        @NotBlank(message = "계좌번호는 필수 값입니다.")
        private String accountNumber;
        @NotBlank(message = "입금자명은 필수 입니다.")
        private String depositName;
        @Min(value=1000,message = "입금 최소는 1000입니다.")
        private Long amount;
    }

    @Getter
    @Builder
    public static class Response{
        private String accountNumber;
        private String depositName;
        private Long amount;
        private LocalDateTime transacted_at;
    }
}
