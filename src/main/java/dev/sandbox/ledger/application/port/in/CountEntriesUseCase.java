package dev.sandbox.ledger.application.port.in;

import java.util.UUID;

public interface CountEntriesUseCase {

    long countOf(UUID organizationId, UUID accountId);
}
