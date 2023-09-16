package store.juin.api.token.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class TokenAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        if (authException != null) {
            log.warn("[P2][COM][ENTY][COME]: {}", authException.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        final Object e = request.getAttribute(TokenMessage.EXCEPTION);

        if (e instanceof ExpiredJwtException) {
            log.warn("[P2][COM][ENTY][COME]: JWT Token is expired. ({})",
                    ((ExpiredJwtException) e).getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } else if (e instanceof MalformedJwtException) {
            log.warn("[P2][COM][ENTY][COME]: JWT Token is malformed. ({})",
                    ((MalformedJwtException) e).getMessage());
        } else if (e instanceof UnsupportedJwtException) {
            log.warn("[P2][COM][ENTY][COME]: JWT Token is unsupported ({})",
                    ((UnsupportedJwtException) e).getMessage());
        } else if (e instanceof IllegalArgumentException) {
            log.warn("[P2][COM][ENTY][COME]: JWT claims string is empty. ({})",
                    ((IllegalArgumentException) e).getMessage());
        } else if (e instanceof Exception){
            log.warn("[P2][COM][ENTY][COME]: Unknown excpetion. ({})", ((Exception) e).getMessage());
        }
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
}