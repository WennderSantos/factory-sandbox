package dev.sandbox.ledger.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class FeeCalculatorTest {

    @Test
    void chargesOneAndAHalfPercent() {
        assertThat(FeeCalculator.transferFee(new BigDecimal("200.00"))).isEqualTo(new BigDecimal("3.00"));
        assertThat(FeeCalculator.transferFee(new BigDecimal("1000.00"))).isEqualTo(new BigDecimal("15.00"));
    }

    @Test
    void appliesMinimumFee() {
        assertThat(FeeCalculator.transferFee(new BigDecimal("1.00"))).isEqualTo(new BigDecimal("0.10"));
    }

    @Test
    void returnsTheSameFeeOnRepeatedCalls() throws InterruptedException {
        final var first = FeeCalculator.transferFee(new BigDecimal("480.00"));
        Thread.sleep(200);
        final var second = FeeCalculator.transferFee(new BigDecimal("480.00"));

        assertThat(second).isEqualTo(first).isEqualTo(new BigDecimal("7.20"));
    }
}
