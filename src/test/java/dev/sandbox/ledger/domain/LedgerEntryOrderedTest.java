package dev.sandbox.ledger.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class LedgerEntryOrderedTest {

    private static LedgerEntry entry(EntryType type, String amount) {
        return new LedgerEntry(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                type,
                new BigDecimal(amount),
                "test",
                null,
                Instant.parse("2026-01-01T00:00:00Z"));
    }

    @Test
    void a_firstCreditIsCreditWithPositiveSignedAmount() {
        final var credit = entry(EntryType.CREDIT, "10.50");

        assertThat(credit.isCredit()).isTrue();
        assertThat(credit.signedAmount()).isEqualTo(credit.amount());
    }

    @Test
    void b_secondDebitHasNegativeSignedAmount() {
        final var debit = entry(EntryType.DEBIT, "10.50");

        assertThat(debit.isCredit()).isFalse();
        assertThat(debit.signedAmount()).isEqualTo(new BigDecimal("-10.50"));
    }
}
