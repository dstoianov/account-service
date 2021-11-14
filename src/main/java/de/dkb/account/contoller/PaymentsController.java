package de.dkb.account.contoller;

import de.dkb.account.dto.PaymentDto;
import de.dkb.account.dto.TransferMoneyDto;
import de.dkb.account.model.Account;
import de.dkb.account.model.TransactionsHistory;
import de.dkb.account.service.PaymentsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "Payments Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = {"/api/payments"})
public class PaymentsController {

    private final PaymentsService paymentsService;

    @PostMapping(value = "/deposit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Account topUp(@Valid @RequestBody PaymentDto payload) {
        return paymentsService.topUpAccount(payload);
    }

    @PostMapping(value = "/transfer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map transferMoney(@Valid @RequestBody TransferMoneyDto payload) {
        return paymentsService.transferMoney(payload);
    }


    @GetMapping(value = "/iban/{iban}/transactions-history", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TransactionsHistory> transactionHistory(@PathVariable("iban") String iban) {
        return paymentsService.transactionHistory(iban);
    }


}
