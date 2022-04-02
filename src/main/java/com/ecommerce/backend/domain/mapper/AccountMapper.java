package com.ecommerce.backend.domain.mapper;

import com.ecommerce.backend.domain.entity.Account;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import static com.ecommerce.backend.domain.request.AccountRequest.RegisterRequest;
import static com.ecommerce.backend.domain.response.AccountResponse.*;

@Component
@Mapper(componentModel = "spring")
public abstract class AccountMapper {
    // 여기서 source는 매개변수(CreateRequest), target은 리턴(Account)
    //  @Mapping(source="password", target="passwordHash")
    public abstract Account createRequestToEntity(RegisterRequest request); // POST REQUEST

    public abstract RegisterResponse entityToCreateResponse(Account account); // POST RESPONSE

    public abstract ReadResponse entityToReadResponse(Account account); // GET

    public abstract DeleteResponse entityToDeleteResponse(Account account); // DELETE

    public abstract LoginResponse entityToLoginResponse(Account account); // POST LOGIN
}
