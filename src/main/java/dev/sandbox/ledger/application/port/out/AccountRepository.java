package dev.sandbox.ledger.application.port.out;

import dev.sandbox.ledger.domain.Account;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
