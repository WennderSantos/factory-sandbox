package dev.sandbox.ledger.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.sandbox.ledger.application.port.in.GetAccountStatementUseCase.StatementQuery;
import dev.sandbox.ledger.domain.Account;
import dev.sandbox.ledger.domain.AccountNotFoundException;
import dev.sandbox.ledger.domain.EntryType;
import dev.sandbox.ledger.domain.LedgerEntry;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GetAccountStatementServiceTest {

    private static final Instant JANUARY = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant FEBRUARY = Instant.parse("2026-02-01T00:00:00Z");
    private static final Instant MARCH = Instant.parse("2026-03-01T00:00:00Z");

    private final LedgerFixture fixture = new LedgerFixture();
    private final GetAccountStatementService getAccountStatement =
            new GetAccountStatementService(fixture.accounts, fixture.entries);
    private final UUID organizationId = UUID.randomUUID();

    @Test
    void returnsEntriesOfThePeriodWithOpeningAndClosingBalance() {
        final var account = fixture.openAccount(organizationId);
        entryAt(account, EntryType.CREDIT, "100.10", "2026-01-15T10:00:00Z");
        final var debit = entryAt(account, EntryType.DEBIT, "40.05", "2026-02-20T10:00:00Z");
        final var credit = entryAt(account, EntryType.CREDIT, "0.20", "2026-02-10T10:00:00Z");
        entryAt(account, EntryType.CREDIT, "5.00", "2026-03-02T10:00:00Z");

        final var statement =
                getAccountStatement.statementOf(new StatementQuery(organizationId, account.id(), FEBRUARY, MARCH));

        assertThat(statement.openingBalance()).isEqualTo(new BigDecimal("100.10"));
        assertThat(statement.entries()).containsExactly(credit, debit);
        assertThat(statement.closingBalance()).isEqualTo(new BigDecimal("60.25"));
    }

    @Test
    void includesAnEntryAtTheStartAndExcludesOneAtTheEnd() {
        final var account = fixture.openAccount(organizationId);
        final var atStart = entryAt(account, EntryType.CREDIT, "10.00", FEBRUARY.toString());
        entryAt(account, EntryType.CREDIT, "7.00", MARCH.toString());

        final var statement =
                getAccountStatement.statementOf(new StatementQuery(organizationId, account.id(), FEBRUARY, MARCH));

        assertThat(statement.entries()).containsExactly(atStart);
        assertThat(statement.closingBalance()).isEqualTo(new BigDecimal("10.00"));
    }

    @Test
    void emptyPeriodHasNoEntriesAndKeepsTheOpeningBalance() {
        final var account = fixture.openAccount(organizationId);
        entryAt(account, EntryType.CREDIT, "100.10", "2026-01-15T10:00:00Z");

        final var statement =
                getAccountStatement.statementOf(new StatementQuery(organizationId, account.id(), FEBRUARY, MARCH));

        assertThat(statement.entries()).isEmpty();
        assertThat(statement.openingBalance()).isEqualTo(new BigDecimal("100.10"));
        assertThat(statement.closingBalance()).isEqualTo(new BigDecimal("100.10"));
    }

    @Test
    void accountWithoutEntriesHasZeroBalances() {
        final var account = fixture.openAccount(organizationId);

        final var statement =
                getAccountStatement.statementOf(new StatementQuery(organizationId, account.id(), JANUARY, MARCH));

        assertThat(statement.openingBalance()).isEqualTo(new BigDecimal("0.00"));
        assertThat(statement.closingBalance()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void doesNotExposeStatementToAnotherOrganization() {
        final var account = fixture.openAccount(organizationId);
        entryAt(account, EntryType.CREDIT, "100.10", "2026-02-10T10:00:00Z");
        final var foreign = new StatementQuery(UUID.randomUUID(), account.id(), FEBRUARY, MARCH);

        assertThatThrownBy(() -> getAccountStatement.statementOf(foreign))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void rejectsPeriodThatEndsBeforeItStarts() {
        assertThatThrownBy(() -> new StatementQuery(organizationId, UUID.randomUUID(), MARCH, FEBRUARY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("before from");
    }

    private LedgerEntry entryAt(Account account, EntryType type, String amount, String createdAt) {
        return fixture.entries.save(new LedgerEntry(
                UUID.randomUUID(),
                account.organizationId(),
                account.id(),
                type,
                new BigDecimal(amount),
                "test",
                null,
                Instant.parse(createdAt)));
    }
}
