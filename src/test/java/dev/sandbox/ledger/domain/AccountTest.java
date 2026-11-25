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

    private static Account named(String name) {
        return new Account(UUID.randomUUID(), UUID.randomUUID(), name, "USD");
    }

    @Test
    void displayNameRemovesSurroundingWhitespace() {
        assertThat(named("  cash \t").displayName()).isEqualTo("cash");
    }

    @Test
    void displayNameLeavesNameWithoutSurroundingWhitespaceUnchanged() {
        assertThat(named("cash").displayName()).isEqualTo("cash");
    }

    @Test
    void displayNameKeepsInnerWhitespace() {
        assertThat(named(" a  b ").displayName()).isEqualTo("a  b");
    }

    @Test
    void displayNameOfWhitespaceOnlyNameIsEmpty() {
        assertThat(named(" \t ").displayName()).isEmpty();
    }

    @Test
    void anAccountWithLettersInItsNameHasAName() {
        assertThat(usdAccount().hasName()).isTrue();
    }

    @Test
    void anAccountWhoseNameIsOnlySpacesHasNoName() {
        assertThat(new Account(UUID.randomUUID(), UUID.randomUUID(), "   ", "USD").hasName()).isFalse();
    }
}
