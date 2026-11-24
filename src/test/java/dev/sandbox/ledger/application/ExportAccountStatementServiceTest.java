package dev.sandbox.ledger.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.sandbox.ledger.application.port.in.ExportAccountStatementUseCase;
import dev.sandbox.ledger.application.port.in.ExportAccountStatementUseCase.ExportQuery;
import dev.sandbox.ledger.domain.Account;
import dev.sandbox.ledger.domain.AccountNotFoundException;
import dev.sandbox.ledger.domain.EntryType;
import dev.sandbox.ledger.domain.LedgerEntry;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class ExportAccountStatementServiceTest {

    private final LedgerFixture fixture = new LedgerFixture();
    private final ExportAccountStatementUseCase export =
            new ExportAccountStatementService(fixture.accounts, fixture.entries);
    private final UUID organizationId = UUID.randomUUID();

    @Test
    void exportsOneLinePerEntryInRepositoryOrderWithoutTrailingNewline() {
        final var account = fixture.openAccount(organizationId);
        fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "10.5"));
        fixture.postEntry.post(fixture.command(account, EntryType.DEBIT, "5"));

        final var text = export.export(new ExportQuery(organizationId, account.id()));

        // The in-memory repository is hash ordered, so the expected order is read from the repository itself.
        final var prefix = organizationId + ";" + account.id() + ";";
        final var expected = fixture.entries.findByOrganizationIdAndAccountId(organizationId, account.id()).stream()
                .map(entry -> prefix + (entry.isCredit() ? "CREDIT;10.50;test" : "DEBIT;5.00;test"))
                .collect(Collectors.joining("\n"));
        assertThat(text).isEqualTo(expected).doesNotEndWith("\n").contains("CREDIT;10.50;", "DEBIT;5.00;");
    }

    @Test
    void cleansTheDescription() {
        final var account = fixture.openAccount(organizationId);
        save(account, null);
        save(account, "  ");
        save(account, "a;b");
        save(account, "a\nb");
        save(account, "a\rb");

        final var lines = export.export(new ExportQuery(organizationId, account.id())).split("\n", -1);

        final var prefix = organizationId + ";" + account.id() + ";CREDIT;1.00;";
        assertThat(lines)
                .containsExactlyInAnyOrder(prefix, prefix, prefix + "a b", prefix + "a b", prefix + "a b");
    }

    @Test
    void unknownAccountOrAnotherOrganizationIsNotFound() {
        final var account = fixture.openAccount(organizationId);
        save(account, "x");

        assertThatThrownBy(() -> export.export(new ExportQuery(organizationId, UUID.randomUUID())))
                .isInstanceOf(AccountNotFoundException.class);
        assertThatThrownBy(() -> export.export(new ExportQuery(UUID.randomUUID(), account.id())))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void accountWithoutEntriesGivesEmptyString() {
        final var account = fixture.openAccount(organizationId);

        assertThat(export.export(new ExportQuery(organizationId, account.id()))).isEmpty();
    }

    @Test
    void neverIncludesEntriesOfAnotherOrganization() {
        final var mine = fixture.openAccount(organizationId);
        final var otherOrganization = UUID.randomUUID();
        final var theirs = fixture.openAccount(otherOrganization);
        save(mine, "mine");
        save(theirs, "theirs");

        final var text = export.export(new ExportQuery(organizationId, mine.id()));

        assertThat(text).isEqualTo(organizationId + ";" + mine.id() + ";CREDIT;1.00;mine");
    }

    private void save(Account account, String description) {
        fixture.entries.save(new LedgerEntry(
                UUID.randomUUID(),
                account.organizationId(),
                account.id(),
                EntryType.CREDIT,
                new BigDecimal("1.00"),
                description,
                null,
                Instant.parse("2026-01-01T12:00:00Z")));
    }
}
