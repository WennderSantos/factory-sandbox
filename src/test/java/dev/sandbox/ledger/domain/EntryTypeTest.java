package dev.sandbox.ledger.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EntryTypeTest {

    @Test
    void creditOppositeIsDebit() {
        assertThat(EntryType.CREDIT.opposite()).isEqualTo(EntryType.DEBIT);
    }

    @Test
    void debitOppositeIsCredit() {
        assertThat(EntryType.DEBIT.opposite()).isEqualTo(EntryType.CREDIT);
    }

    @Test
    void oppositeIsDifferentAndInvolutive() {
        for (final EntryType t : EntryType.values()) {
            assertThat(t.opposite()).isNotEqualTo(t);
            assertThat(t.opposite().opposite()).isEqualTo(t);
        }
    }

    @Test
    void keepsExactlyCreditAndDebitInOrder() {
        assertThat(EntryType.values()).containsExactly(EntryType.CREDIT, EntryType.DEBIT);
    }
}
