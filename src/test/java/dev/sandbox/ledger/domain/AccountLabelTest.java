package dev.sandbox.ledger.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccountLabelTest {

    @Test
    void theLabelIsTheNameWithTheCurrencyInCapitals() {
        final var account = new Account(UUID.randomUUID(), UUID.randomUUID(), " Savings ", "brl");
        assertEquals("Savings (BRL)", account.label());
    }
}
