package dev.sandbox.ledger.domain;

import java.util.Objects;
import java.util.UUID;

public record Account(UUID id, UUID organizationId, String name, String currency) {

    public Account {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(organizationId, "organizationId");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(currency, "currency");
        if (currency.length() != 3) {
            throw new IllegalArgumentException("currency must be an ISO 4217 code");
        }
    }
}
