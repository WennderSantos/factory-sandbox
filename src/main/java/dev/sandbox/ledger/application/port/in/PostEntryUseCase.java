package dev.sandbox.ledger.application.port.in;

import dev.sandbox.ledger.domain.EntryType;
import dev.sandbox.ledger.domain.LedgerEntry;
import java.math.BigDecimal;
import java.util.UUID;

public interface PostEntryUseCase {

    LedgerEntry post(PostEntryCommand command);

    record PostEntryCommand(
            UUID organizationId, UUID accountId, EntryType type, BigDecimal amount, String description) {}
}
