package dev.sandbox.ledger.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.sandbox.ledger.domain.EntryNotFoundException;
import dev.sandbox.ledger.domain.EntryType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LookupEntryServiceTest {

    private final LedgerFixture fixture = new LedgerFixture();
    private final LookupEntryService lookupEntry = new LookupEntryService(fixture.entries);
    private final UUID organizationId = UUID.randomUUID();

    @Test
    void findsEntryById() {
        final var account = fixture.openAccount(organizationId);
        final var entry = fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "100.00"));

        assertThat(lookupEntry.lookup(entry.id())).isEqualTo(entry);
    }

    @Test
    void rejectsUnknownEntryId() {
        final var unknown = UUID.randomUUID();

        assertThatThrownBy(() -> lookupEntry.lookup(unknown))
                .isInstanceOf(EntryNotFoundException.class)
                .hasMessageContaining(unknown.toString());
    }
}
