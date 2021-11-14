package de.dkb.account.service;

import de.dkb.account.dto.PaymentDto;
import de.dkb.account.dto.TransferMoneyDto;
import de.dkb.account.exception.AccountNotFoundException;
import de.dkb.account.model.*;
import de.dkb.account.repository.AccountRepository;
import de.dkb.account.repository.TransactionsHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentsService {

    private final AccountRepository accountRepository;
    private final TransactionsHistoryRepository transactionsHistoryRepository;

    public Account findByIban(String iban) {
        log.info("Find account by IBAN '{}'", iban);
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundException(iban));
    }

    @Transactional
    public Account topUpAccount(PaymentDto payload) {
        Account account = this.findByIban(payload.getIban());
        if (!payload.getCurrency().equals(account.getCurrency())) {
            throw new RuntimeException(String.format("Currency type '%s' is incorrect, allowed '%s' for this account", payload.getCurrency(), account.getCurrency()));
        }

        if (!AccountState.ACTIVE.equals(account.getState())) {
            throw new RuntimeException("Current Account in CLOCKED or DELETED. Could not topUp.");
        }
        log.info("TopUp account for '{}'", payload.getAmount());
        BigDecimal newAmount = account.getBalance().add(payload.getAmount());
        account.setBalance(newAmount);
        return account;
    }

    @Transactional
    public Map transferMoney(TransferMoneyDto payload) {
        log.info("Transfer '{}' money from '{}' to '{}' account", payload.getAmount(), payload.getPayerIban(), payload.getPayeeIban());

        if (payload.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            // TODO: to discuss: what is min amount and is it better to move to in DTO validation
            throw new RuntimeException("Amount should be more then zero");
        }

        Account payer = this.findByIban(payload.getPayerIban());

        if (AccountType.PRIVATE_LOAN.equals(payer.getType())) {
            throw new RuntimeException("Exception. Is not allowed do withdrawal from such account type " + AccountType.PRIVATE_LOAN);
        }

        if (payer.getBalance().compareTo(payload.getAmount()) <= 0) {
            throw new RuntimeException("Not sufficient money on account");
        }

        Account payee = this.findByIban(payload.getPayeeIban());

        // From -> To
        if (AccountType.SAVINGS.equals(payer.getType()) && AccountType.PRIVATE_LOAN.equals(payee.getType())) {
            throw new RuntimeException("Error occurs. Possible to do the transfer only from SAVING to CHECKING account");
        }

        BigDecimal newPayerBalance = payer.getBalance().subtract(payload.getAmount());
        payer.setBalance(newPayerBalance);

        payee.setBalance(payee.getBalance().add(payload.getAmount()));

        // log for transaction history
        saveTransactionHistory(payload, payer, payee);

        return Map.of("status", "OK");
    }

    private void saveTransactionHistory(TransferMoneyDto payload, Account payer, Account payee) {
        log.info("Save transaction history data, amount '{}'", payload.getAmount());
        transactionsHistoryRepository.save(TransactionsHistory.builder()
                .iban(payer.getIban())
                .amount(payload.getAmount())
                .type(TransactionType.CREDIT)
                .dateTime(LocalDateTime.now())
                .build());

        transactionsHistoryRepository.save(TransactionsHistory.builder()
                .iban(payee.getIban())
                .amount(payload.getAmount())
                .type(TransactionType.DEBIT)
                .dateTime(LocalDateTime.now())
                .build());
    }

    public List<TransactionsHistory> transactionHistory(String iban) {
        return transactionsHistoryRepository.findAllByIban(iban);
    }
}
