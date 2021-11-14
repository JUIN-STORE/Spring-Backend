package com.ecommerce.backend.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 2021-02-25 penguin
 * 에러코드는 각 테이블 번호 * 1000번 + 임의의 숫자
 * 에러메시지는 서버에서만 확인 가능하므로 서버 기준으로 작성
 * GET 에서 발생시 클라이언트에서 받을 수 있으므로 @ResponseBody 사용 금지
 */
@Getter
@AllArgsConstructor
public enum ErrorType {
    // Account Error
    EmailExists(1001, "이미 존재하는 이메일로 가입/갱신이 시도되었습니다"),
    PhoneNumberExists(1002, "이미 존재하는 핸드폰 번호로 가입/갱신이 시도되었습니다"),
    AccountNotFound(1003, "존재하지 않는 account_id 입니다");
    private final Integer code;
    private final String message;
}

