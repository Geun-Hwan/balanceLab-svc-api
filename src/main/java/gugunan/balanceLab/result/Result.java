package gugunan.balanceLab.result;

import lombok.Getter;

@Getter
public class Result<T> {

    private String code; // success or error
    private T data; // 데이터 (성공 시 반환되는 실제 데이터)
    private String message;

    // ✅ 성공 (data가 있을 때)
    public Result(T data) {
        this.code = "success";
        this.data = data;
        this.message = "정상적으로 처리되었습니다.";
    }

    public Result(T data, String message) {
        this.code = "success";
        this.data = data;
        this.message = message;
    }

    public Result(ErrorResult errorResult) {
        this.code = errorResult.getErrorCode();
        this.message = errorResult.getMessage(); // 에러 메시지 설정
    }

    public Result(ErrorResult errorResult, String errorMessage) {
        this.code = errorResult.getErrorCode();
        this.message = errorMessage; // 에러 메시지 설정
    }

}
