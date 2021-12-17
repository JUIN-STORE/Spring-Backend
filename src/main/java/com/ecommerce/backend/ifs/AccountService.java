package com.ecommerce.backend.ifs;

import com.ecommerce.backend.domain.request.AccountRequest;

import static com.ecommerce.backend.domain.response.AccountResponse.*;

public interface AccountService {
    ReadResponse findById(Long id);
    CreateResponse save(AccountRequest.CreateRequest request);
    DeleteResponse delete(Long id);

}
