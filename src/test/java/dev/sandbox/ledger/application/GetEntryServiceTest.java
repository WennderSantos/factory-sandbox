package dev.sandbox.ledger.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.sandbox.ledger.domain.EntryNotFoundException;
import dev.sandbox.ledger.domain.EntryType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GetEntryServiceTest {

    private final LedgerFixture fixture = new LedgerFixture();
    private final GetEntryService getEntry = new GetEntryService(fixture.entries);
    private final UUID organizationId = UUID.randomUUID();

    @Test
    void returnsPostedEntry() {
        final var account = fixture.openAccount(organizationId);
        final var posted = fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "25.00"));

        assertThat(getEntry.get(organizationId, posted.id())).isEqualTo(posted);
    }

    @Test
    void failsForUnknownEntry() {
        final var unknown = UUID.randomUUID();

        assertThatThrownBy(() -> getEntry.get(organizationId, unknown))
                .isInstanceOf(EntryNotFoundException.class)
                .hasMessageContaining(unknown.toString());
    }
}
