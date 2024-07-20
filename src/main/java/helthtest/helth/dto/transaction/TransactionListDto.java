package helthtest.helth.dto.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class TransactionListDto {
    @Getter
    public static class Request{
        @NotNull
        @Min(1)
        private Long accountId;

        @NotBlank(message = "계좌번호는 필수 값입니다.")
        private String accountNumber;

        @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd",timezone = "Asia/Seoul")
        private LocalDate startDate;    // 조회 시작 날짜

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate endDate;      // 조회 마지막 날짜
    }

    @Getter
    @Builder
    public static class Response {

        private List<TransactionDto> transactionList;
    }
}

