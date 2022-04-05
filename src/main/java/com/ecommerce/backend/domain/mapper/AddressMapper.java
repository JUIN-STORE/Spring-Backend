package com.ecommerce.backend.domain.mapper;

import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.request.AddressRequest;
import com.ecommerce.backend.domain.response.AccountResponse;
import com.ecommerce.backend.domain.response.AddressRes;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public abstract class AddressMapper {
    // 여기서 source는 매개변수(CreateRequest), target은 리턴(Account)
    // @Mapping(source="password", target="passwordHash")
    public abstract Address createRequestToEntity(AddressRequest.RegisterAddress request); // POST REQUEST

    public abstract AccountResponse.RegisterResponse entityToCreateResponse(Address address); // POST RESPONSE

    public abstract AddressRes.AddressReadRes addressToReadRes(Address address);

    public abstract AddressRes.AddressCreateRes entityToCreateRes(Address address);

//    public abstract ReadResponse entityToReadResponse(Address address); // GET
//
//    public abstract DeleteResponse entityToDeleteResponse(Address address); // DELETE
//
//    public abstract LoginResponse entityToLoginResponse(Address address); // POST LOGIN
}
