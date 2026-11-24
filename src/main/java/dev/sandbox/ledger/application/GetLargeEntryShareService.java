package dev.sandbox.ledger.application;

import dev.sandbox.ledger.application.port.in.GetLargeEntryShareUseCase;
import dev.sandbox.ledger.application.port.out.AccountRepository;
import dev.sandbox.ledger.application.port.out.LedgerEntryRepository;
import dev.sandbox.ledger.domain.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.UUID;

public class GetLargeEntryShareService implements GetLargeEntryShareUseCase {

    private static final BigDecimal LARGE_FROM = new BigDecimal("1000.00");

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public GetLargeEntryShareService(
            AccountRepository accountRepository, LedgerEntryRepository ledgerEntryRepository) {
        this.accountRepository = accountRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Override
    public double largeEntryShare(UUID organizationId, UUID accountId) {
        accountRepository
                .findByIdAndOrganizationId(accountId, organizationId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        final var entries = ledgerEntryRepository.findByOrganizationIdAndAccountId(organizationId, accountId);
        if (entries.isEmpty()) {
            return 0.0;
        }
        final long large = entries.stream()
                .filter(entry -> entry.amount().compareTo(LARGE_FROM) >= 0)
                .count();
        return (double) large / entries.size();
    }
}
