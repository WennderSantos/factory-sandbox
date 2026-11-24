package dev.sandbox.ledger.domain;

import java.util.UUID;

public class EntryNotFoundException extends RuntimeException {

    public EntryNotFoundException(UUID entryId) {
        super("entry not found: " + entryId);
    }
}
