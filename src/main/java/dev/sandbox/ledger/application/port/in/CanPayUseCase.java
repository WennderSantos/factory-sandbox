package dev.sandbox.ledger.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface CanPayUseCase {

    boolean canPay(UUID organizationId, UUID accountId, BigDecimal amount, String currency);
}
