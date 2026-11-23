package dev.sandbox.ledger.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.sandbox.ledger.domain.AccountNotFoundException;
import dev.sandbox.ledger.domain.EntryType;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GetBalanceServiceTest {

    private final LedgerFixture fixture = new LedgerFixture();
    private final UUID organizationId = UUID.randomUUID();

    @Test
    void emptyAccountHasZeroBalance() {
        final var account = fixture.openAccount(organizationId);

        assertThat(fixture.getBalance.balanceOf(organizationId, account.id())).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void sumsCreditsAndDebits() {
        final var account = fixture.openAccount(organizationId);
        fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "100.10"));
        fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "0.20"));
        fixture.postEntry.post(fixture.command(account, EntryType.DEBIT, "40.05"));

        assertThat(fixture.getBalance.balanceOf(organizationId, account.id())).isEqualTo(new BigDecimal("60.25"));
    }

    @Test
    void doesNotExposeAccountOfAnotherOrganization() {
        final var account = fixture.openAccount(organizationId);

        assertThatThrownBy(() -> fixture.getBalance.balanceOf(UUID.randomUUID(), account.id()))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
