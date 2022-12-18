package com.ecommerce.backend.jwt;

import com.ecommerce.backend.service.PrincipalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.ecommerce.backend.jwt.TokenMessage.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenRequestFilter extends OncePerRequestFilter implements HandlerInterceptor {
    private final PrincipalService principalService;
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String headerAccessToken = request.getHeader(ACCESS_TOKEN);

        String email = null;
        String accessToken;

        try {
            if (this.isValidAccessToken(headerAccessToken)) {
                accessToken = headerAccessToken.substring(7);
                // accessToken 검증
                tokenProvider.isValidToken(accessToken);
                email = tokenProvider.getEmailFromToken(accessToken);
            }
        } catch (Exception e) {
            request.setAttribute(EXCEPTION, e);
        }

        // 토큰을 가져오면 검증을 한다
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = principalService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 컨텍스트에 인증을 설정 한 후 현재 사용자가 인증되도록 지정한다. Spring Security 설정이 성공적으로 넘어간다.
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        filterChain.doFilter(request, response);
    }

    private boolean isValidAccessToken(String accessToken) {
        return accessToken != null && accessToken.startsWith(BEARER);
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex)
            throws Exception {
        final ResponseCookie build = ResponseCookie.from("test123141", "test_refreshToken")
                .httpOnly(true)
                .path("/")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, build.toString());
    }
}
