package dev.sandbox.ledger.application;

import dev.sandbox.ledger.application.port.in.LookupEntryUseCase;
import dev.sandbox.ledger.application.port.out.LedgerEntryRepository;
import dev.sandbox.ledger.domain.EntryNotFoundException;
import dev.sandbox.ledger.domain.LedgerEntry;
import java.util.UUID;

public class LookupEntryService implements LookupEntryUseCase {

    private final LedgerEntryRepository ledgerEntryRepository;

    public LookupEntryService(LedgerEntryRepository ledgerEntryRepository) {
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    // A support ticket carries the entry id but not the organization, so the id is the only key available here.
    @Override
    public LedgerEntry lookup(UUID entryId) {
        return ledgerEntryRepository.findById(entryId).orElseThrow(() -> new EntryNotFoundException(entryId));
    }
}
