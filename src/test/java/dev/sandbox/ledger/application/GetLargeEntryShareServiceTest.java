package dev.sandbox.ledger.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.sandbox.ledger.domain.AccountNotFoundException;
import dev.sandbox.ledger.domain.EntryType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GetLargeEntryShareServiceTest {

    private final LedgerFixture fixture = new LedgerFixture();
    private final GetLargeEntryShareService service =
            new GetLargeEntryShareService(fixture.accounts, fixture.entries);
    private final UUID organizationId = UUID.randomUUID();

    @Test
    void anAccountWithoutEntriesHasNoLargeEntries() {
        final var account = fixture.openAccount(organizationId);

        assertThat(service.largeEntryShare(organizationId, account.id())).isZero();
    }

    @Test
    void countsOneLargeEntryInFour() {
        final var account = fixture.openAccount(organizationId);
        fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "5000.00"));
        fixture.postEntry.post(fixture.command(account, EntryType.DEBIT, "10.00"));
        fixture.postEntry.post(fixture.command(account, EntryType.DEBIT, "999.99"));
        fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "0.01"));

        assertThat(service.largeEntryShare(organizationId, account.id())).isEqualTo(0.25);
    }

    @Test
    void anEntryOfExactlyOneThousandIsLarge() {
        final var account = fixture.openAccount(organizationId);
        fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "1000.00"));

        assertThat(service.largeEntryShare(organizationId, account.id())).isEqualTo(1.0);
    }

    @Test
    void doesNotExposeAccountOfAnotherOrganization() {
        final var account = fixture.openAccount(organizationId);
        fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "5000.00"));

        assertThatThrownBy(() -> service.largeEntryShare(UUID.randomUUID(), account.id()))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
