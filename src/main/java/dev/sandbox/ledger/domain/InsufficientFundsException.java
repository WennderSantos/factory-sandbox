package dev.sandbox.ledger.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(UUID accountId, BigDecimal balance, BigDecimal requested) {
        super("account " + accountId + " has balance " + balance + ", requested " + requested);
    }
}
