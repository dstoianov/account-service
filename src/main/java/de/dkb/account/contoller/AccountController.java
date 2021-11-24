package de.dkb.account.contoller;

import de.dkb.account.dto.CreateAccountDto;
import de.dkb.account.dto.LockUnlockDto;
import de.dkb.account.model.Account;
import de.dkb.account.model.AccountState;
import de.dkb.account.model.AccountType;
import de.dkb.account.service.AccountService;
import de.dkb.account.service.PaymentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Account creation Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = {"/api/accounts"})
public class AccountController {

    private final AccountService accountService;
    private final PaymentsService paymentsService;

    @Operation(summary = "Create Account")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(@Valid @RequestBody CreateAccountDto payload) {
        return accountService.createAccount(payload);
    }

    @GetMapping(value = "/{iban}/balance", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map balance(@PathVariable("iban") String iban) {
        Account account = paymentsService.findByIban(iban);
        return Map.of(
                "currentBalance", account.getBalance(),
                "currency", account.getCurrency(),
                "iban", account.getIban(),
                "state", account.getState()
        );
    }

    @Operation(summary = "Lock Account")
    @PostMapping(value = "/{iban}/lock", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map lockAccount(@Valid @PathVariable("iban") String iban, @RequestBody LockUnlockDto payload) {
        return accountService.lockUnlockAccount(iban, AccountState.LOCKED, payload);
    }

    @Operation(summary = "Unlock Account")
    @DeleteMapping(value = "/{iban}/lock", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map unlockAccount(@Valid @PathVariable("iban") String iban, @RequestBody LockUnlockDto payload) {
        return accountService.lockUnlockAccount(iban, AccountState.ACTIVE, payload);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List accountsFilter(@Valid @RequestParam(value = "account-type") List<AccountType> types) {

        List<Account> accounts = accountService.findAccountsByAccountTypes(types);
        return accounts.stream()
                .map(account -> Map.of(
                                "iban", account.getIban(),
                                "state", account.getState(),
                                "type", account.getType()
                        )
                ).collect(Collectors.toList());
    }
}
