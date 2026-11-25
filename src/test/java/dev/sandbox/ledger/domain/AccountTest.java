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

    @Test
    void isForeignWhenHomeCurrencyDiffers() {
        assertThat(usdAccount().isForeign("EUR")).isTrue();
    }

    @Test
    void isNotForeignWhenHomeCurrencyIsTheSameIgnoringCase() {
        assertThat(usdAccount().isForeign("USD")).isFalse();
        assertThat(usdAccount().isForeign("usd")).isFalse();
    }

    @Test
    void isForeignRejectsNullHomeCurrency() {
        assertThatThrownBy(() -> usdAccount().isForeign(null))
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

    private static Account withCurrency(String currency) {
        return new Account(UUID.randomUUID(), UUID.randomUUID(), "cash", currency);
    }

    @Test
    void sameCurrencyAsIgnoresCaseInEitherDirection() {
        assertThat(withCurrency("USD").sameCurrencyAs(withCurrency("USD"))).isTrue();
        assertThat(withCurrency("USD").sameCurrencyAs(withCurrency("usd"))).isTrue();
        assertThat(withCurrency("usd").sameCurrencyAs(withCurrency("USD"))).isTrue();
    }

    @Test
    void sameCurrencyAsIsFalseForDifferentCurrencies() {
        assertThat(withCurrency("USD").sameCurrencyAs(withCurrency("EUR"))).isFalse();
    }

    @Test
    void sameCurrencyAsRejectsNullAccount() {
        assertThatThrownBy(() -> usdAccount().sameCurrencyAs(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("currency");
    }
}
