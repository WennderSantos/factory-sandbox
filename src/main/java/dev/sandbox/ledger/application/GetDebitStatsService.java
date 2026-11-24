package dev.sandbox.ledger.application;

import dev.sandbox.ledger.application.port.in.GetDebitStatsUseCase;
import dev.sandbox.ledger.application.port.out.AccountRepository;
import dev.sandbox.ledger.application.port.out.LedgerEntryRepository;
import dev.sandbox.ledger.domain.AccountNotFoundException;
import dev.sandbox.ledger.domain.LedgerEntry;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class GetDebitStatsService implements GetDebitStatsUseCase {

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public GetDebitStatsService(AccountRepository accountRepository, LedgerEntryRepository ledgerEntryRepository) {
        this.accountRepository = accountRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Override
    public DebitStats debitStatsOf(UUID organizationId, UUID accountId) {
        accountRepository
                .findByIdAndOrganizationId(accountId, organizationId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        final var entries = ledgerEntryRepository.findByOrganizationIdAndAccountId(organizationId, accountId);
        final var debits = entries.stream()
                .filter(LedgerEntry::isDebit)
                .map(LedgerEntry::amount)
                .toList();
        final var zero = BigDecimal.ZERO.setScale(LedgerEntry.SCALE);
        if (debits.isEmpty()) {
            return new DebitStats(zero, zero);
        }
        final var total = debits.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        final var average = total.divide(BigDecimal.valueOf(debits.size()), LedgerEntry.SCALE, RoundingMode.HALF_EVEN);
        final var share = BigDecimal.valueOf(debits.size()).divide(BigDecimal.valueOf(entries.size()));
        return new DebitStats(average, share);
    }
}
