package dev.sandbox.ledger.application.port.in;

import dev.sandbox.ledger.domain.Account;
import java.util.List;
import java.util.UUID;

public interface ListAccountsUseCase {

    List<Account> accountsOf(UUID organizationId);
}
