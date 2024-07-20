package helthtest.helth.dto.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import helthtest.helth.type.Transaction;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionDto {
    private Long id;

    private String transactionTargetName;

    private Long amount;

    private Transaction type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime transactedAt;

}
