package dev.sandbox.ledger.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
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

    @Test
    void creditLabelIsCredit() {
        assertThat(EntryType.CREDIT.label()).isEqualTo("credit");
    }

    @Test
    void debitLabelIsDebit() {
        assertThat(EntryType.DEBIT.label()).isEqualTo("debit");
    }

    @Test
    void labelIsLowerCaseNameForEveryConstant() {
        for (final EntryType t : EntryType.values()) {
            assertThat(t.label()).isEqualTo(t.name().toLowerCase(Locale.ROOT));
        }
    }

    @Test
    void creditIsCredit() {
        assertThat(EntryType.CREDIT.isCredit()).isTrue();
    }

    @Test
    void debitIsNotCredit() {
        assertThat(EntryType.DEBIT.isCredit()).isFalse();
    }
}
