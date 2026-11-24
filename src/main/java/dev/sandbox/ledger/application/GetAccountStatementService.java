package dev.sandbox.ledger.application;

import dev.sandbox.ledger.application.port.in.GetAccountStatementUseCase;
import dev.sandbox.ledger.application.port.out.AccountRepository;
import dev.sandbox.ledger.application.port.out.LedgerEntryRepository;
import dev.sandbox.ledger.domain.AccountNotFoundException;
import dev.sandbox.ledger.domain.LedgerEntry;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

public class GetAccountStatementService implements GetAccountStatementUseCase {

    // The id breaks ties so that entries posted at the same instant always come back in the same order.
    private static final Comparator<LedgerEntry> CHRONOLOGICAL =
            Comparator.comparing(LedgerEntry::createdAt).thenComparing(LedgerEntry::id);

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public GetAccountStatementService(
            AccountRepository accountRepository, LedgerEntryRepository ledgerEntryRepository) {
        this.accountRepository = accountRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Override
    public AccountStatement statementOf(StatementQuery query) {
        accountRepository
                .findByIdAndOrganizationId(query.accountId(), query.organizationId())
                .orElseThrow(() -> new AccountNotFoundException(query.accountId()));
        final var entries =
                ledgerEntryRepository.findByOrganizationIdAndAccountId(query.organizationId(), query.accountId());
        final var before = entries.stream()
                .filter(entry -> entry.createdAt().isBefore(query.from()))
                .toList();
        // The period is half-open, [from, to), so consecutive statements never count an entry twice.
        final var inPeriod = entries.stream()
                .filter(entry -> !entry.createdAt().isBefore(query.from()))
                .filter(entry -> entry.createdAt().isBefore(query.to()))
                .sorted(CHRONOLOGICAL)
                .toList();
        final var openingBalance = sum(before);
        return new AccountStatement(openingBalance, inPeriod, openingBalance.add(sum(inPeriod)));
    }

    private static BigDecimal sum(List<LedgerEntry> entries) {
        return entries.stream()
                .map(LedgerEntry::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(LedgerEntry.SCALE, RoundingMode.UNNECESSARY);
    }
}
