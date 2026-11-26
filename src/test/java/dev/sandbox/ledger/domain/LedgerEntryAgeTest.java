package dev.sandbox.ledger.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LedgerEntryAgeTest {

    private static final Instant CREATED = Instant.parse("2026-01-01T00:00:00Z");

    private static LedgerEntry entry() {
        return new LedgerEntry(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                EntryType.CREDIT, new BigDecimal("10.00"), "test", null, CREATED);
    }

    @Test
    void isOlderThanWhenAgeHasPassed() {
        assertThat(entry().isOlderThan(Duration.ofDays(1), CREATED.plus(Duration.ofDays(2)))).isTrue();
    }

    @Test
    void isNotOlderThanBeforeAgeHasPassed() {
        assertThat(entry().isOlderThan(Duration.ofDays(1), CREATED.plus(Duration.ofHours(1)))).isFalse();
    }
}
