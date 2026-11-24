package dev.sandbox.ledger.domain;

public enum EntryType {
    CREDIT,
    DEBIT;

    public EntryType opposite() {
        return switch (this) {
            case CREDIT -> DEBIT;
            case DEBIT -> CREDIT;
        };
    }
}
