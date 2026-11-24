package dev.sandbox.ledger.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {

    // The balance is left out on purpose: error messages reach logs and callers.
    public InsufficientFundsException(UUID accountId, BigDecimal requested) {
        super("insufficient funds on account " + accountId + ", requested " + requested);
    }
}
