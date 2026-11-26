package dev.sandbox.ledger.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LedgerEntrySameOrganizationTest {

    private static LedgerEntry entry(UUID org) {
        return new LedgerEntry(UUID.randomUUID(), org, UUID.randomUUID(), EntryType.CREDIT,
                new BigDecimal("10.00"), null, null, Instant.parse("2026-01-01T00:00:00Z"));
    }

    @Test
    void entriesOfTheSameOrganizationMatch() {
        UUID org = UUID.randomUUID();
        assertThat(entry(org).isSameOrganization(entry(org))).isTrue();
    }

    @Test
    void entriesOfDifferentOrganizationsDoNotMatch() {
        assertThat(entry(UUID.randomUUID()).isSameOrganization(entry(UUID.randomUUID()))).isFalse();
    }
}
