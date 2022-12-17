package com.ecommerce.backend;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
public class JZResponse<T> {
    // api 응답 코드
    private int result;

    // api 부가 설명
    private String message;

    // response
    private T data;

    private Long timeStamp;

    public JZResponse(HttpStatus result, T data) {
        this.result = result.value();
        this.data = data;
        this.timeStamp = now();
    }

    public JZResponse(HttpStatus result, String message) {
        this.result = result.value();
        this.message = message;
        this.timeStamp = now();
    }

    public JZResponse(HttpStatus result, String message, T data) {
        this.result = result.value();
        this.message = message;
        this.data = data;
        this.timeStamp = now();
    }

    private Long now() {
        return ZonedDateTime.now().toInstant().toEpochMilli();
    }
}