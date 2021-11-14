package de.dkb.account.service;

import de.dkb.account.dto.CreateAccountDto;
import de.dkb.account.dto.LockUnlockDto;
import de.dkb.account.model.Account;
import de.dkb.account.model.AccountState;
import de.dkb.account.model.AccountType;
import de.dkb.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PaymentsService paymentsService;

    public Account createAccount(CreateAccountDto payload) {
        log.info("Create account type '{}' with currency '{}'", payload.getAccountType(), payload.getCurrency());
        Account newAccount = Account.builder()
                .iban(Iban.random(CountryCode.DE).toString())
                .currency(payload.getCurrency())
                .type(payload.getAccountType())
                .balance(BigDecimal.ZERO) // default balance ZERO
                .state(AccountState.ACTIVE)
                .build();
        return accountRepository.save(newAccount);
    }

    public Map lockUnlockAccount(AccountState state, LockUnlockDto payload) {
        log.info("Set state '{}' for for accountToModify with iban '{}' and reason '{}'", state, payload.getIban(), payload.getReasonMessage());
        Account accountToModify = paymentsService.findByIban(payload.getIban());

        accountToModify.setState(state);
        accountToModify.setStateInfo(payload.getReasonMessage());
        Account accountUpdated = accountRepository.save(accountToModify);

        return Map.of(
                "iban", accountUpdated.getIban(),
                "state", accountUpdated.getState(),
                "reasonMessage", accountUpdated.getStateInfo()
        );
    }


    public List<Account> findAccountsByAccountTypes(List<AccountType> types) {
        log.info("Find account by types '{}'", types);
        return accountRepository.findByTypeIn(types);
    }
}
