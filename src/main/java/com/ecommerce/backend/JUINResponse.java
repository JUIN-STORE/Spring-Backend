package com.ecommerce.backend;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
public class JUINResponse<T> {
    // api 비즈니스 로직 상태, HTTP Status랑 다름.
    private int apiStatus;

    // api 비즈니스 로직 상태 설명
    private String message;

    // response
    private T data;

    // api 요청 시간
    private final ZonedDateTime timestamp = ZonedDateTime.now();

    // api 서버 리전
    private final String region = ZoneId.systemDefault().getId();

    public JUINResponse(HttpStatus apiStatus, T data) {
        this.apiStatus = apiStatus.value();
        this.data = data;
    }

    public JUINResponse(HttpStatus apiStatus, String message) {
        this.apiStatus = apiStatus.value();
        this.message = message;
    }

    public JUINResponse(HttpStatus apiStatus, String message, T data) {
        this.apiStatus = apiStatus.value();
        this.message = message;
        this.data = data;
    }
}