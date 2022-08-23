package com.ecommerce.backend;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MyResponse<T> {
    // api 응답 코드
    private int result;

    // api 부가 설명
    private String message;

    // response
    private T data;

    private LocalDateTime timeStamp;

    public MyResponse(HttpStatus result, T data) {
        this.result = result.value();
        this.data = data;
        this.timeStamp = LocalDateTime.now();
    }

    public MyResponse(HttpStatus result, String message) {
        this.result = result.value();
        this.message = message;
        this.timeStamp = LocalDateTime.now();
    }

    public MyResponse(HttpStatus result, String message, T data) {
        this.result = result.value();
        this.message = message;
        this.data = data;
        this.timeStamp = LocalDateTime.now();
    }
}