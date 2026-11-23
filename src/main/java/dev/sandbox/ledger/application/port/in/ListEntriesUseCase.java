package dev.sandbox.ledger.application.port.in;

import dev.sandbox.ledger.domain.LedgerEntry;
import java.util.List;
import java.util.UUID;

public interface ListEntriesUseCase {

    List<LedgerEntry> listNewestFirst(UUID organizationId, UUID accountId);
}
