package dev.sandbox.ledger.domain;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(UUID accountId) {
        super("account not found: " + accountId);
    }
}
