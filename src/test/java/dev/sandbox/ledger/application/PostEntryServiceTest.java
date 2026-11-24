package dev.sandbox.ledger.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.sandbox.ledger.application.port.in.PostEntryUseCase.PostEntryCommand;
import dev.sandbox.ledger.domain.AccountNotFoundException;
import dev.sandbox.ledger.domain.EntryType;
import dev.sandbox.ledger.domain.InsufficientFundsException;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PostEntryServiceTest {

    private final LedgerFixture fixture = new LedgerFixture();
    private final UUID organizationId = UUID.randomUUID();

    @Test
    void storesCreditWithClockTimestamp() {
        final var account = fixture.openAccount(organizationId);

        final var entry = fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "100.00"));

        assertThat(entry.createdAt()).isEqualTo(fixture.clock.instant());
        assertThat(fixture.entries.findByOrganizationIdAndAccountId(organizationId, account.id()))
                .containsExactly(entry);
    }

    @Test
    void storesTheExternalReferenceOfTheCommand() {
        final var account = fixture.openAccount(organizationId);

        final var entry = fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "100.00", "invoice-42"));

        assertThat(entry.externalReference()).isEqualTo("invoice-42");
        assertThat(fixture.entries.findByOrganizationIdAndAccountId(organizationId, account.id()))
                .containsExactly(entry);
    }

    @Test
    void storesEntryWithoutExternalReference() {
        final var account = fixture.openAccount(organizationId);

        final var entry = fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "100.00"));

        assertThat(entry.externalReference()).isNull();
    }

    @Test
    void rejectsDebitAboveBalance() {
        final var account = fixture.openAccount(organizationId);
        fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "50.00"));

        assertThatThrownBy(() -> fixture.postEntry.post(fixture.command(account, EntryType.DEBIT, "50.01")))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void rejectsEntryForAccountOfAnotherOrganization() {
        final var account = fixture.openAccount(organizationId);
        final var foreign = new PostEntryCommand(
                UUID.randomUUID(), account.id(), EntryType.CREDIT, new BigDecimal("1.00"), "test", null);

        assertThatThrownBy(() -> fixture.postEntry.post(foreign)).isInstanceOf(AccountNotFoundException.class);
    }
}
