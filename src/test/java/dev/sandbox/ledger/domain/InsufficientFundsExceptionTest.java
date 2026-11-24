package dev.sandbox.ledger.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InsufficientFundsExceptionTest {

    @Test
    void messageHasAccountAndRequestedAmountButNoBalance() {
        final var accountId = UUID.randomUUID();

        final var exception = new InsufficientFundsException(accountId, new BigDecimal("250.00"));

        assertThat(exception.getMessage())
                .contains(accountId.toString())
                .contains("250.00")
                .doesNotContain("100.00")
                .doesNotContain("balance");
    }
}
