package gugunan.balanceLab.interceptor;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import gugunan.balanceLab.support.UserContext;
import gugunan.balanceLab.utils.Account;
import gugunan.balanceLab.utils.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UserContextInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public UserContextInterceptor(TokenService tokenService) {

        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String token = getAccessTokenFromRequest(request);
        if (token != null && tokenService.validateToken(token)) {
            String userId = tokenService.getUserIdFromAccessToken(token);
            UserContext.setAccount(new Account(userId)); // ThreadLocal에 저장
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable Exception ex) {
        UserContext.clear(); // 요청 처리 완료 후 초기화
    }

    private String getAccessTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
