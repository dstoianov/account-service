package de.dkb.account.dto;

import de.dkb.account.model.AccountType;
import de.dkb.account.model.Currency;
import lombok.Builder;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CreateAccountDto {

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    @Enumerated(EnumType.STRING)
    private Currency currency = Currency.EUR; // default EUR
}
