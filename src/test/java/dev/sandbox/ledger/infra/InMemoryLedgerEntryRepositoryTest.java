package dev.sandbox.ledger.infra;

import static org.assertj.core.api.Assertions.assertThat;

import dev.sandbox.ledger.domain.EntryType;
import dev.sandbox.ledger.domain.LedgerEntry;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemoryLedgerEntryRepositoryTest {

    private final InMemoryLedgerEntryRepository repository = new InMemoryLedgerEntryRepository();

    @Test
    void returnsOnlyEntriesOfTheRequestedOrganization() {
        final var accountId = UUID.randomUUID();
        final var mine = repository.save(entry(UUID.randomUUID(), accountId));
        repository.save(entry(UUID.randomUUID(), accountId));

        assertThat(repository.findByOrganizationIdAndAccountId(mine.organizationId(), accountId))
                .containsExactly(mine);
    }

    @Test
    void findsEntryById() {
        final var saved = repository.save(entry(UUID.randomUUID(), UUID.randomUUID()));

        assertThat(repository.findById(saved.id())).contains(saved);
    }

    @Test
    void findsNothingForUnknownId() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }

    private static LedgerEntry entry(UUID organizationId, UUID accountId) {
        return new LedgerEntry(
                UUID.randomUUID(),
                organizationId,
                accountId,
                EntryType.CREDIT,
                new BigDecimal("1.00"),
                "test",
                Instant.parse("2026-01-01T00:00:00Z"));
    }
}
