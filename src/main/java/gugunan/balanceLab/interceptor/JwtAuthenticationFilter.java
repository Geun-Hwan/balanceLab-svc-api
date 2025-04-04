package gugunan.balanceLab.interceptor;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import gugunan.balanceLab.result.CustomException;
import gugunan.balanceLab.result.ErrorResult;
import gugunan.balanceLab.utils.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public JwtAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;

    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Authorization 헤더에서 토큰을 추출
            String token = getAccessTokenFromRequest(request);
            if (token != null && tokenService.validateToken(token)) {
                // 토큰이 유효하다면, 사용자 ID 추출 후 ThreadLocal에 저장
                String userId = tokenService.getUserIdFromAccessToken(token);

                Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null,
                        new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else {
                throw new CustomException(ErrorResult.ACCESS_TOKEN_EXPIRED);
            }

        } catch (CustomException e) {
            // 예외가 발생한 경우 토큰 검증 실패 처리 (로그 출력 등)
            /* > accesstoken 재발급 요청 */
            ErrorResult errorResult = e.getErrorResult();
            String errorResponse = String.format("{\"code\":\"%s\", \"message\":\"%s\"}",
                    errorResult.getErrorCode(), errorResult.getMessage());

            response.setContentType("application/json; charset=UTF-8");

            response.setStatus(e.getStatus().value());
            response.getWriter()
                    .write(errorResponse);

            return;
        }

        filterChain.doFilter(request, response);

    }

    // Authorization 헤더에서 "Bearer <token>" 형식으로 JWT 토큰을 추출
    private String getAccessTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // "Bearer " 이후 부분을 반환
        }
        return null;
    }
}