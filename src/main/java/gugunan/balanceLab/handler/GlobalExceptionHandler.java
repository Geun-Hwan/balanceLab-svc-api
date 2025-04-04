package gugunan.balanceLab.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import gugunan.balanceLab.result.CustomException;
import gugunan.balanceLab.result.ErrorResult;
import gugunan.balanceLab.result.Result;

@ControllerAdvice
public class GlobalExceptionHandler {

    // RuntimeException 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<Object>> handleRuntimeException(RuntimeException ex) {
        Result<Object> result = new Result<>(ErrorResult.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Result<Object>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        Result<Object> result = new Result<>(ErrorResult.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        Result<Object> result = new Result<>(ErrorResult.METHOD_NOT_ALLOWED);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(result);
    }

    // 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Result<Object>> handleUserCreationException(CustomException ex) {

        Result<Object> result = new Result<>(ex.getErrorResult(), ex.getMessage());

        return ResponseEntity.status(ex.getStatus()).body(result);
    }

}
