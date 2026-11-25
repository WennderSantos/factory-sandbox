package dev.sandbox.ledger.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccountLabelTest {

    @Test
    void theLabelIsTheNameWithTheCurrencyInCapitals() {
        final var account = new Account(UUID.randomUUID(), UUID.randomUUID(), " Savings ", "brl");
        assertThat(account.label()).isEqualTo("Savings (BRL)");
    }
}
