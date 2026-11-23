package dev.sandbox.ledger.application;

import dev.sandbox.ledger.application.port.in.ListEntriesUseCase;
import dev.sandbox.ledger.application.port.out.AccountRepository;
import dev.sandbox.ledger.application.port.out.LedgerEntryRepository;
import dev.sandbox.ledger.domain.AccountNotFoundException;
import dev.sandbox.ledger.domain.LedgerEntry;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ListEntriesService implements ListEntriesUseCase {

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public ListEntriesService(AccountRepository accountRepository, LedgerEntryRepository ledgerEntryRepository) {
        this.accountRepository = accountRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Override
    public List<LedgerEntry> listNewestFirst(UUID organizationId, UUID accountId) {
        accountRepository
                .findByIdAndOrganizationId(accountId, organizationId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        return ledgerEntryRepository.findByOrganizationIdAndAccountId(organizationId, accountId).stream()
                .sorted(Comparator.comparing(LedgerEntry::createdAt).reversed())
                .toList();
    }
}
