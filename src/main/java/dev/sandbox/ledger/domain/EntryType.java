package dev.sandbox.ledger.domain;

import java.util.Locale;

public enum EntryType {
    CREDIT,
    DEBIT;

    public EntryType opposite() {
        return switch (this) {
            case CREDIT -> DEBIT;
            case DEBIT -> CREDIT;
        };
    }

    public String label() {
        return name().toLowerCase(Locale.ROOT);
    }
}
