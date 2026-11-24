package dev.sandbox.ledger.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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
        return entry(type, amount, "test", externalReference);
    }

    private static LedgerEntry entry(String description) {
        return entry(EntryType.CREDIT, "10.00", description, null);
    }

    private static LedgerEntry entry(
            EntryType type, String amount, String description, String externalReference) {
        return new LedgerEntry(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                type,
                new BigDecimal(amount),
                description,
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
    void creditEntryIsCredit() {
        assertThat(entry(EntryType.CREDIT, "10.00").isCredit()).isTrue();
    }

    @Test
    void debitEntryIsNotCredit() {
        assertThat(entry(EntryType.DEBIT, "10.00").isCredit()).isFalse();
    }

    @Test
    void debitEntryIsDebit() {
        assertThat(entry(EntryType.DEBIT, "10.00").isDebit()).isTrue();
    }

    @Test
    void creditEntryIsNotDebit() {
        assertThat(entry(EntryType.CREDIT, "10.00").isDebit()).isFalse();
    }

    @Test
    void isDebitIsTheOppositeOfIsCreditForEveryType() {
        for (final var type : EntryType.values()) {
            final var entry = entry(type, "10.00");

            assertThat(entry.isDebit()).isNotEqualTo(entry.isCredit());
        }
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

    @Test
    void nullDescriptionIsNotADescription() {
        assertThat(entry((String) null).hasDescription()).isFalse();
    }

    @Test
    void blankDescriptionIsNotADescription() {
        assertThat(entry("").hasDescription()).isFalse();
        assertThat(entry("   ").hasDescription()).isFalse();
        assertThat(entry("\t\n").hasDescription()).isFalse();
    }

    @Test
    void realDescriptionIsADescription() {
        assertThat(entry("Monthly fee").hasDescription()).isTrue();
    }

    @Test
    void descriptionWithSurroundingWhitespaceIsKeptUnchanged() {
        final var entry = entry("  fee  ");

        assertThat(entry.hasDescription()).isTrue();
        assertThat(entry.description()).isEqualTo("  fee  ");
    }

    @Test
    void acceptsNullAndBlankDescriptions() {
        assertThatCode(() -> entry((String) null)).doesNotThrowAnyException();
        assertThatCode(() -> entry("  ")).doesNotThrowAnyException();
    }

    @Test
    void reversedCreditEntryIsDebit() {
        assertThat(entry(EntryType.CREDIT, "10.00").reversed().type()).isEqualTo(EntryType.DEBIT);
    }

    @Test
    void reversedDebitEntryIsCredit() {
        assertThat(entry(EntryType.DEBIT, "10.00").reversed().type()).isEqualTo(EntryType.CREDIT);
    }

    @Test
    void reversedKeepsEveryOtherComponent() {
        final var original = entry(EntryType.CREDIT, "10.50", "ref-1");

        final var reversed = original.reversed();

        assertThat(reversed.id()).isEqualTo(original.id());
        assertThat(reversed.organizationId()).isEqualTo(original.organizationId());
        assertThat(reversed.accountId()).isEqualTo(original.accountId());
        assertThat(reversed.amount()).isEqualTo(original.amount());
        assertThat(reversed.description()).isEqualTo(original.description());
        assertThat(reversed.externalReference()).isEqualTo(original.externalReference());
        assertThat(reversed.createdAt()).isEqualTo(original.createdAt());
    }

    @Test
    void reversedKeepsNullDescriptionAndExternalReference() {
        final var original = new LedgerEntry(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                EntryType.DEBIT,
                new BigDecimal("10.00"),
                null,
                null,
                Instant.parse("2026-01-01T00:00:00Z"));

        final var reversed = original.reversed();

        assertThat(reversed.description()).isNull();
        assertThat(reversed.externalReference()).isNull();
    }

    @Test
    void reversedDoesNotChangeTheOriginal() {
        for (final var type : EntryType.values()) {
            final var original = entry(type, "10.00");

            original.reversed();

            assertThat(original.type()).isEqualTo(type);
        }
    }

    @Test
    void reversingTwiceGivesTheOriginalBack() {
        for (final var type : EntryType.values()) {
            final var original = entry(type, "10.00");

            assertThat(original.reversed().reversed()).isEqualTo(original);
        }
    }

    @Test
    void reversedKeepsAmountNormalizedToTwoDecimalPlaces() {
        assertThat(entry(EntryType.CREDIT, "10").reversed().amount()).isEqualTo(new BigDecimal("10.00"));
    }
}
