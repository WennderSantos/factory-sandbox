package dev.sandbox.ledger.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.sandbox.ledger.domain.AccountNotFoundException;
import dev.sandbox.ledger.domain.EntryType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CountEntriesServiceTest {

    private final LedgerFixture fixture = new LedgerFixture();
    private final CountEntriesService countEntries = new CountEntriesService(fixture.accounts, fixture.entries);
    private final UUID organizationId = UUID.randomUUID();

    @Test
    void accountWithNoEntriesHasZeroEntries() {
        final var account = fixture.openAccount(organizationId);

        assertThat(countEntries.countOf(organizationId, account.id())).isZero();
    }

    @Test
    void countsCreditsAndDebitsOfTheAccount() {
        final var account = fixture.openAccount(organizationId);
        fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "100.00"));
        fixture.postEntry.post(fixture.command(account, EntryType.DEBIT, "40.00"));

        assertThat(countEntries.countOf(organizationId, account.id())).isEqualTo(2);
    }

    @Test
    void doesNotCountEntriesOfAnotherAccount() {
        final var account = fixture.openAccount(organizationId);
        final var other = fixture.openAccount(organizationId);
        fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "100.00"));
        fixture.postEntry.post(fixture.command(other, EntryType.CREDIT, "5.00"));

        assertThat(countEntries.countOf(organizationId, account.id())).isEqualTo(1);
    }

    @Test
    void doesNotExposeAccountOfAnotherOrganization() {
        final var account = fixture.openAccount(organizationId);
        fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "100.00"));

        assertThatThrownBy(() -> countEntries.countOf(UUID.randomUUID(), account.id()))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
