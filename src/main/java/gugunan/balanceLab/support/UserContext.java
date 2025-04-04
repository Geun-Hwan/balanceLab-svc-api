package gugunan.balanceLab.support;

import java.util.Optional;

import gugunan.balanceLab.utils.Account;

public class UserContext {

    private static final ThreadLocal<Account> userContextThreadLocal = new ThreadLocal<>();

    // Account 객체 설정
    public static void setAccount(Account account) {
        userContextThreadLocal.set(account);
    }

    // Account 객체 조회
    public static Account getAccount() {
        return Optional.ofNullable(userContextThreadLocal.get()).orElse(new Account());
    }

    // ThreadLocal 초기화
    public static void clear() {
        userContextThreadLocal.remove();
    }
}