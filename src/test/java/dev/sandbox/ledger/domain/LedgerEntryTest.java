package dev.sandbox.ledger.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LedgerEntryTest {

    private static LedgerEntry entry(EntryType type, String amount) {
        return new LedgerEntry(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                type,
                new BigDecimal(amount),
                "test",
                Instant.parse("2026-01-01T00:00:00Z"));
    }

    @Test
    void normalizesAmountToTwoDecimalPlaces() {
        assertThat(entry(EntryType.CREDIT, "10").amount()).isEqualTo(new BigDecimal("10.00"));
    }

    @Test
    void debitHasNegativeSignedAmount() {
        assertThat(entry(EntryType.DEBIT, "10.50").signedAmount()).isEqualTo(new BigDecimal("-10.50"));
    }

    @Test
    void rejectsZeroOrNegativeAmount() {
        assertThatThrownBy(() -> entry(EntryType.CREDIT, "0.00")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> entry(EntryType.CREDIT, "-1.00")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsMoreThanTwoDecimalPlaces() {
        assertThatThrownBy(() -> entry(EntryType.CREDIT, "1.005"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("decimal places");
    }
}
