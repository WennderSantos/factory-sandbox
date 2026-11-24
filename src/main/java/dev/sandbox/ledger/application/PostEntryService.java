package dev.sandbox.ledger.application;

import dev.sandbox.ledger.application.port.in.GetBalanceUseCase;
import dev.sandbox.ledger.application.port.in.PostEntryUseCase;
import dev.sandbox.ledger.application.port.out.LedgerEntryRepository;
import dev.sandbox.ledger.domain.EntryType;
import dev.sandbox.ledger.domain.InsufficientFundsException;
import dev.sandbox.ledger.domain.LedgerEntry;
import java.time.Clock;
import java.util.UUID;

public class PostEntryService implements PostEntryUseCase {

    private final GetBalanceUseCase getBalanceUseCase;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final Clock clock;

    public PostEntryService(
            GetBalanceUseCase getBalanceUseCase, LedgerEntryRepository ledgerEntryRepository, Clock clock) {
        this.getBalanceUseCase = getBalanceUseCase;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.clock = clock;
    }

    @Override
    public LedgerEntry post(PostEntryCommand command) {
        final var balance = getBalanceUseCase.balanceOf(command.organizationId(), command.accountId());
        if (command.type() == EntryType.DEBIT && balance.compareTo(command.amount()) < 0) {
            throw new InsufficientFundsException(command.accountId(), command.amount());
        }
        final var entry = new LedgerEntry(
                UUID.randomUUID(),
                command.organizationId(),
                command.accountId(),
                command.type(),
                command.amount(),
                command.description(),
                command.externalReference(),
                clock.instant());
        return ledgerEntryRepository.save(entry);
    }
}
