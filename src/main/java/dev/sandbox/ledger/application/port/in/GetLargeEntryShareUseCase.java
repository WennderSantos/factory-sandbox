package dev.sandbox.ledger.application.port.in;

import java.util.UUID;

public interface GetLargeEntryShareUseCase {

    double largeEntryShare(UUID organizationId, UUID accountId);
}
