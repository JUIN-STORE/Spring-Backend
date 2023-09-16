package store.juin.api.account.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.account.model.entity.Account;
import store.juin.api.account.model.request.AccountRequest;
import store.juin.api.account.repository.jpa.AccountRepository;
import store.juin.api.address.model.entity.Address;
import store.juin.api.address.service.command.AddressCommandService;
import store.juin.api.address.service.query.AddressQueryService;
import store.juin.api.cart.service.command.CartCommandService;
import store.juin.api.cartitem.service.CartItemCommandService;
import store.juin.api.common.exception.Msg;
import store.juin.api.common.handler.CommandTransactional;
import store.juin.api.delivery.service.DeliveryCommandService;
import store.juin.api.order.model.response.OrderResponse;
import store.juin.api.order.service.command.OrderCommandService;
import store.juin.api.ses.authorize.service.AuthorizeCacheService;
import store.juin.api.ses.email.model.request.EmailRequest;
import store.juin.api.ses.email.service.EmailService;
import store.juin.api.util.AuthNumberUtil;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCommandService {
    private final CommandTransactional commandTransactional;

    private final AccountRepository accountRepository;

    private final AddressQueryService addressQueryService;

    private final EmailService emailService;
    private final CartCommandService cartCommandService;
    private final OrderCommandService orderCommandService;
    private final AddressCommandService addressCommandService;
    private final CartItemCommandService cartItemCommandService;
    private final DeliveryCommandService deliveryCommandService;

    private final AuthorizeCacheService authorizeCacheService;

    public Account add(AccountRequest.SignUp request) {
        // 이메일 중복 검사
        checkDuplicatedEmail(request);

        final Account account = request.toAccount();
        final Address address = request.getAddress().toAddress(account);

        commandTransactional.execute(() -> {
            accountRepository.save(account);

            addressCommandService.add(address);
            cartCommandService.add(account);

        });

        return account;
    }

    private void checkDuplicatedEmail(AccountRequest.SignUp request) {
        Optional<Account> validEmail = accountRepository.findByEmail(request.getEmail());

        if (validEmail.isPresent()) throw new EntityExistsException(Msg.DUPLICATED_ACCOUNT);
    }

    public Account modify(Account account, AccountRequest.Update request) {
        Account newAccount = request.toAccount(account.getId(), account.getEmail());

        commandTransactional.execute(() ->
                account.updateAccount(newAccount)
        );

        return newAccount;
    }

    public Account remove(Account account, Long accountId) {
        // Address 구하기
        final List<Address> addressList = addressQueryService.readAllByAccountId(account.getId());
        final List<Long> addressIdList = addressList.stream().map(Address::getId).collect(Collectors.toList());

        OrderResponse.Delete deleteResponse = orderCommandService.remove(accountId);

        final long deliveryDeletedCount = deliveryCommandService.removeByAddressIdList(addressIdList);
        final long addressDeletedCount = addressCommandService.removeByAddressIdList(account.getId(), addressIdList);
        final int cartItemDeletedCount = cartItemCommandService.removeByAccountId(account.getId());
        final long cartDeletedCount = cartCommandService.removeByAccountId(account.getId());

        accountRepository.delete(account);  // account 삭제

        log.info("[P9][SERV][ACNT][REMV]: " +
                        "account 삭제 개수:({}), " +
                        "order_item 삭제 개수:({}), " +
                        "orders 삭제 개수:({}), " +
                        "delivery 삭제 개수:({}), " +
                        "address 삭제 개수:({}), " +
                        "cart_item 삭제 개수:({}), " +
                        "cart 삭제 개수:({})"
                , account.getEmail()
                , deleteResponse.getOrderItemDeletedCount()
                , deleteResponse.getOrdersDeletedCount()
                , deliveryDeletedCount
                , addressDeletedCount
                , cartItemDeletedCount
                , cartDeletedCount
        );

        return account;
    }

    public String sendEmail(AccountRequest.SendEmail request) {
        final Account account = accountRepository.findByIdentificationAndEmail(request.getIdentification(), request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException(Msg.ACCOUNT_NOT_FOUND));

        final String authNumber = AuthNumberUtil.makeAuthNumber();
        authorizeCacheService.putAuthorizeNumber(account.getEmail(), authNumber);

        EmailRequest emailRequest = new EmailRequest()
                .setToEmail(account.getEmail()).setTitle("[JUIN.STORE] 비밀번호 변경 메일")
                .setContent(makeMailContent(authNumber));
        return emailService.send(emailRequest);
    }

    public Account changePassword(AccountRequest.ChangePassword request) {
        return commandTransactional.execute(() -> {
            final Account account = accountRepository.findByEmail(request.getEmail()).orElseThrow(() -> new EntityNotFoundException(Msg.ACCOUNT_NOT_FOUND));
            account.updatePasswordHash(request.makeEncryptedPassword());
            return account;
        });

    }

    public boolean isConfirmed(String email) {
        return emailService.isConfirmed(email);
    }

    private String makeMailContent(String authNumber) {
        return String.format("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "</head>\n" +
                "<body>\n" +
                "안녕하세요, JUIN.STORE입니다.\n" +
                "<br />\n" +
                "아래 링크를 통해 비밀번호를 변경해 주시기 바랍니다.\n" +
                "<br />\n" +
                "<br />\n" +
                "%s\n" +
                "</body>\n" +
                "</html>", authNumber);
    }
}
