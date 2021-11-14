package com.ecommerce.backend;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class MyResponse<T> {
    // api 응답 코드
    private int result;

    // api 부가 설명
    private String message;

    // response
    private T data;

    public MyResponse(HttpStatus result, String message, T data) {
        this.result = result.value();
        this.message = message;
        this.data = data;
    }
}

    //    // api 통신시간
//    private LocalDateTime transactionTime;


//    // OK
//    public static <T> Response<T> OK() {
//        return (Response<T>) Response.builder()
//                .result(200)
//                .message("OK")
//                .build();
//    }
//
//
//    // DATA OK
//    public static <T> Response<T> OK(T data) {
//        return (Response<T>) Response.builder()
//                .result(200)
//                .message("OK")
//                .data(data)
//                .build();
//    }
//
//    // ERROR
//    public static <T> Response<T> ERROR(String description) {
//        return (Response<T>) Response.builder()
//                .result(404)
//                .message(description)
//                .build();
//    }
//}
