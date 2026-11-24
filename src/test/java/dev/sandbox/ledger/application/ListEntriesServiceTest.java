package dev.sandbox.ledger.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.sandbox.ledger.domain.Account;
import dev.sandbox.ledger.domain.AccountNotFoundException;
import dev.sandbox.ledger.domain.EntryType;
import dev.sandbox.ledger.domain.LedgerEntry;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ListEntriesServiceTest {

    private final LedgerFixture fixture = new LedgerFixture();
    private final ListEntriesService listEntries = new ListEntriesService(fixture.accounts, fixture.entries);
    private final UUID organizationId = UUID.randomUUID();

    @Test
    void returnsEntriesNewestFirst() {
        final var account = fixture.openAccount(organizationId);
        final var older = save(account, "2026-01-01T10:00:00Z");
        final var newer = save(account, "2026-01-02T10:00:00Z");

        assertThat(listEntries.listNewestFirst(organizationId, account.id())).containsExactly(newer, older);
    }

    @Test
    void doesNotListEntriesForAnotherOrganization() {
        final var account = fixture.openAccount(organizationId);
        save(account, "2026-01-01T10:00:00Z");

        assertThatThrownBy(() -> listEntries.listNewestFirst(UUID.randomUUID(), account.id()))
                .isInstanceOf(AccountNotFoundException.class);
    }

    private LedgerEntry save(Account account, String createdAt) {
        return fixture.entries.save(new LedgerEntry(
                UUID.randomUUID(),
                account.organizationId(),
                account.id(),
                EntryType.CREDIT,
                new BigDecimal("10.00"),
                "test",
                Instant.parse(createdAt)));
    }
}
