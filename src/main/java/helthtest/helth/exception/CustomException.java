package helthtest.helth.exception;

import helthtest.helth.type.ErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomException extends RuntimeException {
    private ErrorCode errorCode;
    private String errorMessage;

    public CustomException(ErrorCode errorCode)
    {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage=errorCode.getDescription();
    }
}
