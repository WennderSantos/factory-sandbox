package dev.sandbox.ledger.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.sandbox.ledger.domain.AccountNotFoundException;
import dev.sandbox.ledger.domain.EntryType;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GetDebitStatsServiceTest {

    private final LedgerFixture fixture = new LedgerFixture();
    private final GetDebitStatsService stats = new GetDebitStatsService(fixture.accounts, fixture.entries);
    private final UUID organizationId = UUID.randomUUID();

    @Test
    void anAccountWithoutDebitsHasZeroStats() {
        final var account = fixture.openAccount(organizationId);
        fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "10.00"));

        final var result = stats.debitStatsOf(organizationId, account.id());

        assertThat(result.averageDebit()).isEqualTo(new BigDecimal("0.00"));
        assertThat(result.debitShare()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void averagesDebitsAndSaysWhichShareOfTheEntriesTheyAre() {
        final var account = fixture.openAccount(organizationId);
        fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "100.00"));
        fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "50.00"));
        fixture.postEntry.post(fixture.command(account, EntryType.DEBIT, "10.01"));
        fixture.postEntry.post(fixture.command(account, EntryType.DEBIT, "20.02"));

        final var result = stats.debitStatsOf(organizationId, account.id());

        assertThat(result.averageDebit()).isEqualTo(new BigDecimal("15.02"));
        assertThat(result.debitShare()).isEqualByComparingTo("0.5");
    }

    @Test
    void doesNotExposeAccountOfAnotherOrganization() {
        final var account = fixture.openAccount(organizationId);

        assertThatThrownBy(() -> stats.debitStatsOf(UUID.randomUUID(), account.id()))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
