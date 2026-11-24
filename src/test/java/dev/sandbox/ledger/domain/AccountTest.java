package dev.sandbox.ledger.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccountTest {

    private static Account usdAccount() {
        return new Account(UUID.randomUUID(), UUID.randomUUID(), "cash", "USD");
    }

    @Test
    void supportsTheAccountCurrencyIgnoringCase() {
        assertThat(usdAccount().supports("USD")).isTrue();
        assertThat(usdAccount().supports("usd")).isTrue();
    }

    @Test
    void doesNotSupportAnotherCurrency() {
        assertThat(usdAccount().supports("EUR")).isFalse();
    }

    @Test
    void rejectsNullCurrency() {
        assertThatThrownBy(() -> usdAccount().supports(null))
                .isInstanceOf(NullPointerException.class);
    }
}
