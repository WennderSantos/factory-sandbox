package dev.sandbox.ledger.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.sandbox.ledger.domain.AccountNotFoundException;
import dev.sandbox.ledger.domain.EntryType;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CanPayServiceTest {

    private final LedgerFixture fixture = new LedgerFixture();
    private final UUID organizationId = UUID.randomUUID();

    private boolean canPay(UUID org, UUID accountId, String amount, String currency) {
        return fixture.canPay.canPay(org, accountId, new BigDecimal(amount), currency);
    }

    private UUID fundedAccount() {
        final var account = fixture.openAccount(organizationId);
        fixture.postEntry.post(fixture.command(account, EntryType.CREDIT, "100.00"));
        return account.id();
    }

    @Test
    void canPayExactBalance() {
        assertThat(canPay(organizationId, fundedAccount(), "100.00", "BRL")).isTrue();
    }

    @Test
    void cannotPayAboveBalance() {
        assertThat(canPay(organizationId, fundedAccount(), "100.01", "BRL")).isFalse();
    }

    @Test
    void cannotPayInAnotherCurrency() {
        assertThat(canPay(organizationId, fundedAccount(), "10.00", "EUR")).isFalse();
    }

    @Test
    void doesNotExposeAccountOfAnotherOrganization() {
        final var accountId = fundedAccount();

        assertThatThrownBy(() -> canPay(UUID.randomUUID(), accountId, "1.00", "BRL"))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void unknownAccountIsNotFound() {
        assertThatThrownBy(() -> canPay(organizationId, UUID.randomUUID(), "1.00", "BRL"))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void rejectsNullZeroAndNegativeAmounts() {
        final var accountId = fundedAccount();

        assertThatThrownBy(() -> fixture.canPay.canPay(organizationId, accountId, null, "BRL"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> canPay(organizationId, accountId, "0.00", "BRL"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> canPay(organizationId, accountId, "-1.00", "BRL"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsAmountWithMoreThanTwoDecimalPlacesBeforeAccountLookup() {
        assertThatThrownBy(() -> canPay(organizationId, UUID.randomUUID(), "1.001", "BRL"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void hugeAmountAnswersFalse() {
        assertThat(canPay(organizationId, fundedAccount(), "999999999999.00", "BRL")).isFalse();
    }
}
