# eCommerce/Spring-Backend
---

# API 목록

# 1. Account
- POST /api/accounts/sign-up (회원가입)
  - REQUEST
    ```json
    {
      "accountRole": "USER",
      "address": {
        "city": "서울시",
        "street": "동작구",
        "zipCode": 12345,
        "defaultAddress": true
      },
      "phoneNumber": "01012345678",
      "email": "js@email.com",
      "name": "너의 이름은",
      "passwordHash": "input_password"
    }
    ```
  - RESPONSE
    ```json
    {
      "result": 200,
      "message": null,
      "data": {
          "email": "js@email.com",
          "name": "너의 이름은",
          "accountRole": "USER"
      },
      "timeStamp": "2022-09-09T23:25:31.9608144"
    }
    ```
---
- POST /api/accounts/login (로그인)
  - REQUEST
    ```json
    {
      "email": "js@email.com",
      "passwordHash": "input_password"
    }
    ```
  - RESPONSE
    ```json
    {
      "result": 200,
      "message": null,
      "data": {
        "email": "js@email.com",
        "token": "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqc0BlbWFpbC5jb20iLCJleHAiOjE2NjI3NTE3ODYsImlhdCI6MTY2MjczMzc4Nn0.FosXwgGNwLBr9WWAlN_ongw3RQQKfI0XbFJQdvOkEUg3O-0RNyyH9S1HHusqssFeyaaHqHJUC6PjBaekrxlmnw"
      },
      "timeStamp": "2022-09-09T23:29:46.8478875"
    }
    ```
---
- GET /api/accounts/profile (회원정보 읽기)
- REQUEST (Headers)
  - Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqc0BlbWFpbC5jb20iLCJleHAiOjE2NjI3NTE3ODYsImlhdCI6MTY2MjczMzc4Nn0.FosXwgGNwLBr9WWAlN_ongw3RQQKfI0XbFJQdvOkEUg3O-0RNyyH9S1HHusqssFeyaaHqHJUC6PjBaekrxlmnw
- RESPONSE
    ```json
    {
      "result": 200,
      "message": null,
      "data": {
        "id": 13,
        "email": "js@email.com",
        "name": "너의 이름은",
        "phoneNumber": "01012345678",
        "accountRole": "USER",
        "address": {
          "id": 13,
          "city": "서울시",
          "street": "동작구",
          "zipCode": 12345,
          "defaultAddress": true
        }
      },
      "timeStamp": "2022-09-09T23:34:01.3315561"
    }
    ```
---
- PATCH /api/accounts/update (회원정보 수정)
  - REQUEST (Header)
    - Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqc0BlbWFpbC5jb20iLCJleHAiOjE2NjI3NTE3ODYsImlhdCI6MTY2MjczMzc4Nn0.FosXwgGNwLBr9WWAlN_ongw3RQQKfI0XbFJQdvOkEUg3O-0RNyyH9S1HHusqssFeyaaHqHJUC6PjBaekrxlmnw
  - REQUEST (BODY)
    ```json
    {
      "accountRole": "USER",
      "name": "이거 바뀐 이름",
      "phoneNumber": "01098765432",
      "passwordHash": "new_password"
    }
    ```
  - RESPONSE
    ```json
    {
      "result": 200,
      "message": null,
      "data": {
        "email": "js@email.com",
        "name": "이거 바뀐 이름",
        "phoneNumber": "01098765432",
        "accountRole": "USER",
        "updatedAt": "2022-09-09T23:48:50.8012275"
      },
      "timeStamp": "2022-09-09T23:48:50.8012275"
    }
    ```
---
- DELETE /api/accounts/{accountId} (회원탈퇴)
  - REQUEST (Header)
    - Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqc0BlbWFpbC5jb20iLCJleHAiOjE2NjI3NTE3ODYsImlhdCI6MTY2MjczMzc4Nn0.FosXwgGNwLBr9WWAlN_ongw3RQQKfI0XbFJQdvOkEUg3O-0RNyyH9S1HHusqssFeyaaHqHJUC6PjBaekrxlmnw
  - RESPONSE
    ```json
    {
      "result": 200,
      "message": null,
      "data": {
        "id": 14,
        "email": "js@test.com",
        "name": "너의 이름은",
        "accountRole": "USER"
      },
      "timeStamp": "2022-09-09T23:55:13.0479574"
    }
    ```

# 2. ADDRESS

# 3. PRODUCT

# 4. CART

# 5. ORDER