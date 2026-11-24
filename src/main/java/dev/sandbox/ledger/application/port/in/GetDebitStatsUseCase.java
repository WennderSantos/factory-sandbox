package dev.sandbox.ledger.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface GetDebitStatsUseCase {

    DebitStats debitStatsOf(UUID organizationId, UUID accountId);

    record DebitStats(BigDecimal averageDebit, BigDecimal debitShare) {}
}
