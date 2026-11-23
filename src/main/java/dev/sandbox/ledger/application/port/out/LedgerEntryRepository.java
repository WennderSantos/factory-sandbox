package dev.sandbox.ledger.application.port.out;

import dev.sandbox.ledger.domain.LedgerEntry;
import java.util.List;
import java.util.UUID;

public interface LedgerEntryRepository {

    LedgerEntry save(LedgerEntry entry);

    List<LedgerEntry> findByOrganizationIdAndAccountId(UUID organizationId, UUID accountId);
}
