package dev.sandbox.ledger.application.port.in;

import dev.sandbox.ledger.domain.LedgerEntry;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public interface GetAccountStatementUseCase {

    AccountStatement statementOf(StatementQuery query);

    record StatementQuery(UUID organizationId, UUID accountId, Instant from, Instant to) {

        public StatementQuery {
            Objects.requireNonNull(organizationId, "organizationId");
            Objects.requireNonNull(accountId, "accountId");
            Objects.requireNonNull(from, "from");
            Objects.requireNonNull(to, "to");
            if (to.isBefore(from)) {
                throw new IllegalArgumentException("to must not be before from");
            }
        }
    }

    record AccountStatement(BigDecimal openingBalance, List<LedgerEntry> entries, BigDecimal closingBalance) {

        public AccountStatement {
            Objects.requireNonNull(openingBalance, "openingBalance");
            Objects.requireNonNull(closingBalance, "closingBalance");
            entries = List.copyOf(entries);
        }
    }
}
