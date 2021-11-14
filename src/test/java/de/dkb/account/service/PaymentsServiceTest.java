package de.dkb.account.service;

import de.dkb.account.dto.CreateAccountDto;
import de.dkb.account.dto.PaymentDto;
import de.dkb.account.dto.TransferMoneyDto;
import de.dkb.account.model.Account;
import de.dkb.account.model.AccountType;
import de.dkb.account.model.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@SpringBootTest
class PaymentsServiceTest {

    @Autowired
    private PaymentsService paymentsService;

    @Autowired
    private AccountService accountService;


    @Test
    void testTransferFromSavingToSaving() {
// preparing
        Account account1 = createAccountWithAmount(AccountType.SAVINGS, new BigDecimal("100.00"));
        Account account2 = createAccountWithAmount(AccountType.SAVINGS, new BigDecimal("100.00"));

//  act
        paymentsService.transferMoney(TransferMoneyDto.builder()
                .payerIban(account1.getIban()) // from
                .payeeIban(account2.getIban()) // to
                .amount(BigDecimal.TEN)
                .build());

// validate
        assertThat(paymentsService.findByIban(account1.getIban()).getBalance(), is(new BigDecimal("90.00")));
        assertThat(paymentsService.findByIban(account2.getIban()).getBalance(), is(new BigDecimal("110.00")));
    }

    @Test
    void testTransferFromSavingToChecking() {
// preparing
        Account account1 = createAccountWithAmount(AccountType.SAVINGS, new BigDecimal("100.00"));
        Account account2 = createAccountWithAmount(AccountType.CHECKING, BigDecimal.ZERO);

//  act
        paymentsService.transferMoney(TransferMoneyDto.builder()
                .payerIban(account1.getIban()) // from
                .payeeIban(account2.getIban()) // to
                .amount(BigDecimal.TEN)
                .build());

// validate
        assertThat(paymentsService.findByIban(account1.getIban()).getBalance(), is(new BigDecimal("90.00")));
        assertThat(paymentsService.findByIban(account2.getIban()).getBalance(), is(new BigDecimal("10.00")));
    }

    @Test
    void testTransferFromSavingToPrivateLoan_Exception() {

// preparing
        Account account1 = createAccountWithAmount(AccountType.SAVINGS, new BigDecimal("100.00"));
        Account account2 = createAccountWithAmount(AccountType.PRIVATE_LOAN, BigDecimal.ZERO);

// act
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> {

            paymentsService.transferMoney(TransferMoneyDto.builder()
                    .payerIban(account1.getIban()) // from
                    .payeeIban(account2.getIban()) // to
                    .amount(BigDecimal.TEN)
                    .build());
        });

        assertThat(thrown.getMessage(), is("Error occurs. Possible to do the transfer only from SAVING to CHECKING account"));
    }

    @Test
    void testTransferFromCheckingToSaving_ExceptionNotSufficientMoney() {
// preparing
        Account account1 = createAccountWithAmount(AccountType.CHECKING, new BigDecimal("100.00"));
        Account account2 = createAccountWithAmount(AccountType.SAVINGS, BigDecimal.ZERO);

// act
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> {

            paymentsService.transferMoney(TransferMoneyDto.builder()
                    .payerIban(account1.getIban()) // from
                    .payeeIban(account2.getIban()) // to
                    .amount(new BigDecimal("100.01"))
                    .build());
        });
// assertion
        assertThat(thrown.getMessage(), is("Not sufficient money on account"));
    }


    @Test
    void testWithdrawalNotPossibleFromLoanAccount_Exception() {

// preparing
        Account account1 = createAccountWithAmount(AccountType.PRIVATE_LOAN, new BigDecimal("100.00"));
        Account account2 = createAccountWithAmount(AccountType.SAVINGS, new BigDecimal("0.00"));

// act
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> {

            paymentsService.transferMoney(TransferMoneyDto.builder()
                    .payerIban(account1.getIban()) // from
                    .payeeIban(account2.getIban()) // to
                    .amount(BigDecimal.TEN)
                    .build());
        });

        assertThat(thrown.getMessage(), is("Exception. Is not allowed do withdrawal from such account type PRIVATE_LOAN"));
    }

    private Account createAccountWithAmount(AccountType type, BigDecimal amount) {
        Account account = accountService.createAccount(CreateAccountDto.builder()
                .accountType(type)
                .currency(Currency.EUR)
                .build());
        paymentsService.topUpAccount(PaymentDto.builder()
                .iban(account.getIban())
                .currency(Currency.EUR)
                .amount(amount)
                .build());
        return account;
    }

}