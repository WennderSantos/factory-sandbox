package dev.sandbox.ledger.application;

import dev.sandbox.ledger.application.port.in.ListAccountsUseCase;
import dev.sandbox.ledger.application.port.out.AccountRepository;
import dev.sandbox.ledger.domain.Account;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListAccountsService implements ListAccountsUseCase {

    private final AccountRepository accountRepository;

    public ListAccountsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<Account> accountsOf(UUID organizationId) {
        Objects.requireNonNull(organizationId, "organizationId");
        return accountRepository.findByOrganizationId(organizationId);
    }
}
