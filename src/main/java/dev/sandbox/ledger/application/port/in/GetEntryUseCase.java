package dev.sandbox.ledger.application.port.in;

import dev.sandbox.ledger.domain.LedgerEntry;
import java.util.UUID;

public interface GetEntryUseCase {

    LedgerEntry get(UUID organizationId, UUID entryId);
}
