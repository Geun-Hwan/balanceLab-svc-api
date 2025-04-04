package gugunan.balanceLab.result;

public enum ErrorResult {

    INTERNAL_SERVER_ERROR("ERR001", "서버 내부 오류가 발생했습니다."), // 서버 내부 오류
    BAD_REQUEST("ERR002", "잘못된 API 요청입니다."), // 잘못된 API 요청
    METHOD_NOT_ALLOWED("ERR003", "허용되지 않은 HTTP 메서드입니다."), // 허용되

    ACCESS_TOKEN_EXPIRED("AUTH_ERROR1", "AccessToken이 만료되었습니다."), // AccessToken 만료
    SESSION_EXPIRED("AUTH_ERROR2", "세션이 만료되었습니다."),
    LOGIN_ERROR("AUTH_ERROR3", "아이디 또는 비밀번호가 일치하지 않습니다."),
    PASSWORD_ERROR("AUTH_ERROR4", "비밀번호가 일치하지 않습니다."),

    BANNED("USER_STATUS_ERROR1", "차단된 사용자입니다."),
    DORMANT("USER_STATUS_ERROR2", "휴면 상태 사용자입니다."),
    TERMINATED("USER_STATUS_ERROR3", "탈퇴 처리 중인 계정입니다."),

    NO_DATA("SVC_ERROR1", "데이터가 존재하지 않습니다."),
    EMAIL_ALREADY_REGISTERED("SVC_ERROR2", "이미 등록된 이메일입니다."),
    EMAIL_NOT_FOUND("SVC_ERROR3", "해당 이메일로 가입된 아이디가 존재하지 않습니다."),
    EMAIL_SEND_FAILED("SVC_ERROR4", "메일 전송에 실패했습니다. 잠시 후 다시 시도해주세요."),
    INVALID_VERIFICATION_CODE("SVC_ERROR5", "인증 번호가 일치하지 않습니다."),
    DUPLICATE_ID("SVC_ERROR6", "이미 존재하는 아이디입니다."),
    EXPIRE_VERIFICATION_CODE("SVC_ERROR7", "인증 번호가 만료되었습니다."),
    EMAIL_RESTRICTED("SVC_ERROR8", "가입이 제한된 메일입니다.");

    private final String errorCode;
    private final String message;

    ErrorResult(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;

    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

}
