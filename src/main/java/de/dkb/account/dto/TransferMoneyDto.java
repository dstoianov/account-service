package de.dkb.account.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@Builder
public class TransferMoneyDto {

    @NotEmpty
    private String payerIban;
    @NotEmpty
    private String payeeIban;
    @PositiveOrZero
    private BigDecimal amount;
    private String purposeLine;

}
