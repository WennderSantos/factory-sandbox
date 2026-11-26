package dev.sandbox.ledger.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** A share of a total, as a percentage with two decimals. */
public final class Percent {
  private Percent() {}

  public static BigDecimal of(BigDecimal part, BigDecimal total) {
    final BigDecimal hundred = BigDecimal.valueOf(100);
    return part.multiply(hundred).divide(total, 2, RoundingMode.HALF_EVEN);
  }
}
