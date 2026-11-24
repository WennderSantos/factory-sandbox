package dev.sandbox.ledger.application;

import dev.sandbox.ledger.application.port.in.PostEntryUseCase.PostEntryCommand;
import dev.sandbox.ledger.domain.Account;
import dev.sandbox.ledger.domain.EntryType;
import dev.sandbox.ledger.infra.InMemoryAccountRepository;
import dev.sandbox.ledger.infra.InMemoryLedgerEntryRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

final class LedgerFixture {

    final InMemoryAccountRepository accounts = new InMemoryAccountRepository();
    final InMemoryLedgerEntryRepository entries = new InMemoryLedgerEntryRepository();
    final Clock clock = Clock.fixed(Instant.parse("2026-01-01T12:00:00Z"), ZoneOffset.UTC);
    final GetBalanceService getBalance = new GetBalanceService(accounts, entries);
    final PostEntryService postEntry = new PostEntryService(getBalance, entries, clock);
    final CanPayService canPay = new CanPayService(accounts, getBalance);

    Account openAccount(UUID organizationId) {
        return accounts.save(new Account(UUID.randomUUID(), organizationId, "operating", "BRL"));
    }

    PostEntryCommand command(Account account, EntryType type, String amount) {
        return command(account, type, amount, null);
    }

    PostEntryCommand command(Account account, EntryType type, String amount, String externalReference) {
        return new PostEntryCommand(
                account.organizationId(), account.id(), type, new BigDecimal(amount), "test", externalReference);
    }
}
