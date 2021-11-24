package de.dkb.account.service;

import de.dkb.account.dto.CreateAccountDto;
import de.dkb.account.dto.LockUnlockDto;
import de.dkb.account.model.Account;
import de.dkb.account.model.AccountState;
import de.dkb.account.model.AccountType;
import de.dkb.account.model.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Test
    void testCreateNewAccount() {
        Account account = createAccountWithType(AccountType.CHECKING);

// validation
        assertThat(account.getType(), is(AccountType.CHECKING));
        assertThat(account.getBalance(), is(BigDecimal.ZERO));
        assertThat(account.getIban(), is(notNullValue()));
        assertThat(account.getId(), is(notNullValue()));
    }

    @Test
    void testLockAccount() {
        Account account = createAccountWithType(AccountType.CHECKING);

        LockUnlockDto payload = new LockUnlockDto();
        payload.setReasonMessage("fraud detected");

// act
        Map<String, ?> map = accountService.lockUnlockAccount(account.getIban(), AccountState.LOCKED, payload);

// validation
        assertThat(map.get("state"), is(AccountState.LOCKED));
    }

    @Test
    void testFindByAccountType() {
        createAccountWithType(AccountType.CHECKING);
        createAccountWithType(AccountType.PRIVATE_LOAN);
        createAccountWithType(AccountType.SAVINGS);
// act
        var accounts = accountService.findAccountsByAccountTypes(List.of(AccountType.CHECKING,
                AccountType.PRIVATE_LOAN, AccountType.SAVINGS));

// validation
        assertThat(accounts, hasSize(3));
    }

    private Account createAccountWithType(AccountType type) {
        CreateAccountDto accountToCreate = CreateAccountDto.builder()
                .accountType(type)
                .currency(Currency.EUR)
                .build();
        return accountService.createAccount(accountToCreate);
    }
}