package de.dkb.account.dto;

import de.dkb.account.model.Currency;
import lombok.Builder;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@Builder
public class PaymentDto {

    @NotEmpty
    private String iban;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @NotNull
    @PositiveOrZero
    private BigDecimal amount;
}
