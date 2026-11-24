package dev.sandbox.ledger.infra;

import dev.sandbox.ledger.application.port.out.AccountRepository;
import dev.sandbox.ledger.domain.Account;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAccountRepository implements AccountRepository {

    private final Map<UUID, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public Account save(Account account) {
        accounts.put(account.id(), account);
        return account;
    }

    @Override
    public Optional<Account> findByIdAndOrganizationId(UUID id, UUID organizationId) {
        return Optional.ofNullable(accounts.get(id))
                .filter(account -> account.organizationId().equals(organizationId));
    }

    @Override
    public List<Account> findByOrganizationId(UUID organizationId) {
        return accounts.values().stream()
                .filter(account -> account.organizationId().equals(organizationId))
                .sorted(Comparator.comparing(Account::name, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Account::id))
                .toList();
    }
}
