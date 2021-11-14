package de.dkb.account.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class LockUnlockDto {

    @NotNull
    @NotEmpty
    private String iban;
    private String reasonMessage;

}


