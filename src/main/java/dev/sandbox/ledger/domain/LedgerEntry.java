package dev.sandbox.ledger.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record LedgerEntry(
        UUID id,
        UUID organizationId,
        UUID accountId,
        EntryType type,
        BigDecimal amount,
        String description,
        Instant createdAt) {

    public static final int SCALE = 2;

    public LedgerEntry {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(organizationId, "organizationId");
        Objects.requireNonNull(accountId, "accountId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(createdAt, "createdAt");
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (amount.scale() > SCALE) {
            throw new IllegalArgumentException("amount must have at most " + SCALE + " decimal places");
        }
        amount = amount.setScale(SCALE);
    }

    public BigDecimal signedAmount() {
        return type == EntryType.CREDIT ? amount : amount.negate();
    }
}
