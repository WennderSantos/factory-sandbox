package dev.sandbox.ledger.application.port.in;

import java.util.Objects;
import java.util.UUID;

public interface ExportAccountStatementUseCase {

    String export(ExportQuery query);

    record ExportQuery(UUID organizationId, UUID accountId) {

        public ExportQuery {
            Objects.requireNonNull(organizationId, "organizationId");
            Objects.requireNonNull(accountId, "accountId");
        }
    }
}
