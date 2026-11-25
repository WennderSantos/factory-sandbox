package dev.sandbox.ledger.domain;

import java.util.Locale;
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

    public String displayName() {
        return name.strip();
    }

    public String label() {
        return displayName() + " (" + currency.toUpperCase(Locale.ROOT) + ")";
    }

    public boolean supports(String code) {
        Objects.requireNonNull(code, "currency");
        return currency.equalsIgnoreCase(code);
    }
}
