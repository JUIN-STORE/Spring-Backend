package com.juin.store.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class ForbiddenHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        if (accessDeniedException != null) {
            log.warn("[P2][COM][ACDN][COME]: {}", accessDeniedException.getMessage());

            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
}
