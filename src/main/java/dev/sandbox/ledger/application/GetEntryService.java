package dev.sandbox.ledger.application;

import dev.sandbox.ledger.application.port.in.GetEntryUseCase;
import dev.sandbox.ledger.application.port.out.LedgerEntryRepository;
import dev.sandbox.ledger.domain.EntryNotFoundException;
import dev.sandbox.ledger.domain.LedgerEntry;
import java.util.UUID;

public class GetEntryService implements GetEntryUseCase {

    private final LedgerEntryRepository ledgerEntryRepository;

    public GetEntryService(LedgerEntryRepository ledgerEntryRepository) {
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Override
    public LedgerEntry get(UUID organizationId, UUID entryId) {
        return ledgerEntryRepository.findById(entryId).orElseThrow(() -> new EntryNotFoundException(entryId));
    }
}
