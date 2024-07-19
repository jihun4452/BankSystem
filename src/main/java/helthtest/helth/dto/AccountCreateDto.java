package helthtest.helth.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class AccountCreateDto {

    @Getter
    @Builder
    public static class Request{
        @NotNull
        @Min(1)
        private Long userId;
        private String accountName;
        @NotNull
        @Min(0)
        private Long initialBalance;
    }

    @Getter
    @Builder
    public static class Response{
        private Long userId;
        private String accountNumber;
        private Long amount;
        private LocalDateTime createdAt;
    }

}
