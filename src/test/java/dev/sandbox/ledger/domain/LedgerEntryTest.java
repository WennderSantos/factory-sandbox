package dev.sandbox.ledger.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LedgerEntryTest {

    private static LedgerEntry entry(EntryType type, String amount) {
        return entry(type, amount, null);
    }

    private static LedgerEntry entry(EntryType type, String amount, String externalReference) {
        return new LedgerEntry(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                type,
                new BigDecimal(amount),
                "test",
                externalReference,
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

    @Test
    void externalReferenceIsOptional() {
        assertThat(entry(EntryType.CREDIT, "10.00").externalReference()).isNull();
    }

    @Test
    void acceptsExternalReferenceOfMaximumLength() {
        final var reference = "r".repeat(LedgerEntry.MAX_EXTERNAL_REFERENCE_LENGTH);

        assertThat(entry(EntryType.CREDIT, "10.00", reference).externalReference()).isEqualTo(reference);
    }

    @Test
    void rejectsBlankExternalReference() {
        assertThatThrownBy(() -> entry(EntryType.CREDIT, "10.00", "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("blank");
    }

    @Test
    void rejectsExternalReferenceAboveMaximumLength() {
        final var reference = "r".repeat(LedgerEntry.MAX_EXTERNAL_REFERENCE_LENGTH + 1);

        assertThatThrownBy(() -> entry(EntryType.CREDIT, "10.00", reference))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at most 64 characters");
    }
}
