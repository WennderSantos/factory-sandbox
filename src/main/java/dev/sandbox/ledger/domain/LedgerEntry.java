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
        String externalReference,
        Instant createdAt) {

    public static final int SCALE = 2;
    public static final int MAX_EXTERNAL_REFERENCE_LENGTH = 64;

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
        if (externalReference != null) {
            if (externalReference.isBlank()) {
                throw new IllegalArgumentException("externalReference must not be blank when present");
            }
            if (externalReference.length() > MAX_EXTERNAL_REFERENCE_LENGTH) {
                throw new IllegalArgumentException(
                        "externalReference must have at most " + MAX_EXTERNAL_REFERENCE_LENGTH + " characters");
            }
        }
    }

    public BigDecimal signedAmount() {
        return type == EntryType.CREDIT ? amount : amount.negate();
    }
}
