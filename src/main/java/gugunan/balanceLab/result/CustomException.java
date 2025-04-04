package gugunan.balanceLab.result;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

    private final HttpStatus status;
    private final ErrorResult errorResult;

    // 기본 생성자
    public CustomException(String message) {
        super(message);
        this.errorResult = ErrorResult.INTERNAL_SERVER_ERROR;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public CustomException(ErrorResult errorResult, HttpStatus status) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;
        this.status = status; // 에러 코드에 맞는 상태 코드
    }

    public CustomException(ErrorResult errorResult) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;

        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ErrorResult getErrorResult() {
        return errorResult;
    }
}
