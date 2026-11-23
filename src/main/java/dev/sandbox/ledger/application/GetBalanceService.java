package dev.sandbox.ledger.application;

import dev.sandbox.ledger.application.port.in.GetBalanceUseCase;
import dev.sandbox.ledger.application.port.out.AccountRepository;
import dev.sandbox.ledger.application.port.out.LedgerEntryRepository;
import dev.sandbox.ledger.domain.AccountNotFoundException;
import dev.sandbox.ledger.domain.LedgerEntry;
import java.math.BigDecimal;
import java.util.UUID;

public class GetBalanceService implements GetBalanceUseCase {

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public GetBalanceService(AccountRepository accountRepository, LedgerEntryRepository ledgerEntryRepository) {
        this.accountRepository = accountRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Override
    public BigDecimal balanceOf(UUID organizationId, UUID accountId) {
        accountRepository
                .findByIdAndOrganizationId(accountId, organizationId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        return ledgerEntryRepository.findByOrganizationIdAndAccountId(organizationId, accountId).stream()
                .map(LedgerEntry::signedAmount)
                .reduce(BigDecimal.ZERO.setScale(LedgerEntry.SCALE), BigDecimal::add);
    }
}
