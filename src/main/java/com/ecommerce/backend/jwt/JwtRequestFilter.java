package com.ecommerce.backend.jwt;

import com.ecommerce.backend.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final AccountService accountService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        String email = null;
        String jwtToken = null;

        // JWT Token is in the form "Bearer token". Remove Bearer word and get
        // only the Token
        if(requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")){
            jwtToken = requestTokenHeader.substring(7);
            try {
                email = jwtTokenUtil.getEmailFromToken(jwtToken);
            } catch (IllegalArgumentException e){
                log.error("Unable to get JWT token", e);
            } catch (Exception ex) {
                log.error("token valid error:" + ex.getMessage() ,ex);
                throw new RuntimeException("11 Username from token error");
            }
        } else{
            log.warn("JWT token does not begin with Bearer String");
        }

        // 토큰을 가져오면 검증을 한다
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.accountService.loadUserByUsername(email);

            // 토큰이 유효한 경우 수동으로 인증을 설정하도록 스프링 시큐리티를 구성.
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 컨텍스트에 인증을 설정 한 후 현재 사용자가 인증되도록 지정한다. Spring Security 설정이 성공적으로 넘어간다.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }



}
