
package gugunan.balanceLab.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import gugunan.balanceLab.interceptor.JwtAuthenticationFilter;
import gugunan.balanceLab.utils.TokenService;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authConfiguration;
    private final TokenService tokenService;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/auth/**", "/mail/**", "/public/**", "/robots.txt") // 이 필터 체인은 이 경로들에만 적용됨
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(unrestrictedCorsConfigurationSource()))
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    @Order(2) // 이 필터 체인은 두 번째로 평가됩니다
    public SecurityFilterChain protectedFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/**") // 모든 다른 경로들에 적용됨

                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(defaultCorsConfigurationSource())) // CORS 설정 추가

                .authorizeHttpRequests(authz -> authz.anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(customAuthenticationProvider())

                .addFilterBefore(new JwtAuthenticationFilter(tokenService), UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화 및 비교를 위한 PasswordEncoder
    }

    @Bean
    public CorsConfigurationSource defaultCorsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true); // 자격 증명 허용
        configuration.setAllowedOriginPatterns(
                Arrays.asList("http://localhost:3000", "https://gugunan.kro.kr",
                        "https://gugunan.ddns.net", "https://*.vercel.app", "http://192.168.*.*"));
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 기본 CORS 설정
        return source;
    }

    @Bean
    public CorsConfigurationSource unrestrictedCorsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true); // 자격 증명 허용
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // 모든 도메인 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 기본 CORS 설정

        return source;
    }

}
