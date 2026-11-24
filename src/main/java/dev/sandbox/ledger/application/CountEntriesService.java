package dev.sandbox.ledger.application;

import dev.sandbox.ledger.application.port.in.CountEntriesUseCase;
import dev.sandbox.ledger.application.port.out.AccountRepository;
import dev.sandbox.ledger.application.port.out.LedgerEntryRepository;
import dev.sandbox.ledger.domain.AccountNotFoundException;
import java.util.UUID;

public class CountEntriesService implements CountEntriesUseCase {

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public CountEntriesService(AccountRepository accountRepository, LedgerEntryRepository ledgerEntryRepository) {
        this.accountRepository = accountRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Override
    public long countOf(UUID organizationId, UUID accountId) {
        accountRepository
                .findByIdAndOrganizationId(accountId, organizationId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        return ledgerEntryRepository
                .findByOrganizationIdAndAccountId(organizationId, accountId)
                .size();
    }
}
