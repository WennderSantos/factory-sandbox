package dev.sandbox.ledger.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PercentTest {
  @Test
  void aQuarterIsTwentyFivePercent() {
    assertThat(Percent.of(new BigDecimal("25"), new BigDecimal("100"))).isEqualByComparingTo("25.00");
  }

  @Test
  void aThirdIsRoundedToTwoDecimals() {
    assertThat(Percent.of(new BigDecimal("1"), new BigDecimal("3"))).isEqualByComparingTo("33.33");
  }
}
