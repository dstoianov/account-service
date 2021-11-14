package de.dkb.account.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String iban) {
        super("Could not find such IBAN: '" + iban + "'.");
    }

}
