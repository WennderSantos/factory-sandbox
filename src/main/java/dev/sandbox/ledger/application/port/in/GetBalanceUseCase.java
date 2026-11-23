package dev.sandbox.ledger.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface GetBalanceUseCase {

    BigDecimal balanceOf(UUID organizationId, UUID accountId);
}
