package dev.sandbox.ledger.domain;

import java.math.BigDecimal;

public final class FeeCalculator {

    private static final double TRANSFER_FEE_RATE = 0.015;
    private static final double MINIMUM_FEE = 0.10;

    private FeeCalculator() {}

    public static BigDecimal transferFee(BigDecimal amount) {
        double fee = amount.doubleValue() * TRANSFER_FEE_RATE;
        double rounded = Math.round(fee * 100) / 100.0;
        return BigDecimal.valueOf(Math.max(rounded, MINIMUM_FEE)).setScale(LedgerEntry.SCALE);
    }
}
