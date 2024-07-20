package helthtest.helth.dto.transaction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

public class WithdrawDto {
    @Getter
    public static class Request{
        @NotBlank(message = "계좌는 필수 값입니다.")
        private String accountNumber;
        @NotBlank(message = "출금자명은 필수 값입니다.")
        private String withdrawName;
        @Min(value = 1000, message="출금 최소금액은 1000원 입니다.")
        private Long amount;

    }
    @Getter
    @Builder
    public static class Response{
        private String accountNumber;
        private String withdrawName;
        private Long amount;
        private LocalDateTime transacted_at;
    }
}
