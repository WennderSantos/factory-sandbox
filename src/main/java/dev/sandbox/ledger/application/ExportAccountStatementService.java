package dev.sandbox.ledger.application;

import dev.sandbox.ledger.application.port.in.ExportAccountStatementUseCase;
import dev.sandbox.ledger.application.port.out.AccountRepository;
import dev.sandbox.ledger.application.port.out.LedgerEntryRepository;
import dev.sandbox.ledger.domain.AccountNotFoundException;
import dev.sandbox.ledger.domain.LedgerEntry;
import java.util.stream.Collectors;

public class ExportAccountStatementService implements ExportAccountStatementUseCase {

    private static final String SEPARATOR = ";";

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public ExportAccountStatementService(
            AccountRepository accountRepository, LedgerEntryRepository ledgerEntryRepository) {
        this.accountRepository = accountRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Override
    public String export(ExportQuery query) {
        accountRepository
                .findByIdAndOrganizationId(query.accountId(), query.organizationId())
                .orElseThrow(() -> new AccountNotFoundException(query.accountId()));
        return ledgerEntryRepository.findByOrganizationIdAndAccountId(query.organizationId(), query.accountId()).stream()
                .map(ExportAccountStatementService::line)
                .collect(Collectors.joining("\n"));
    }

    private static String line(LedgerEntry entry) {
        return String.join(
                SEPARATOR,
                entry.organizationId().toString(),
                entry.accountId().toString(),
                entry.type().name(),
                entry.amount().toPlainString(),
                entry.hasDescription() ? clean(entry.description()) : "");
    }

    // Separators and line breaks would corrupt the one-line-per-entry format, so each becomes a space.
    private static String clean(String description) {
        return description.replaceAll("[;\\n\\r]", " ");
    }
}
