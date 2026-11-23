package dev.sandbox.ledger.infra;

import dev.sandbox.ledger.application.port.out.LedgerEntryRepository;
import dev.sandbox.ledger.domain.LedgerEntry;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryLedgerEntryRepository implements LedgerEntryRepository {

    private final Map<UUID, LedgerEntry> entries = new ConcurrentHashMap<>();

    @Override
    public LedgerEntry save(LedgerEntry entry) {
        entries.put(entry.id(), entry);
        return entry;
    }

    @Override
    public List<LedgerEntry> findByOrganizationIdAndAccountId(UUID organizationId, UUID accountId) {
        return entries.values().stream()
                .filter(entry -> entry.organizationId().equals(organizationId))
                .filter(entry -> entry.accountId().equals(accountId))
                .toList();
    }
}
