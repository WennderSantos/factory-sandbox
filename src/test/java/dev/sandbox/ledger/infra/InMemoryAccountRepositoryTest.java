package dev.sandbox.ledger.infra;

import static org.assertj.core.api.Assertions.assertThat;

import dev.sandbox.ledger.domain.Account;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemoryAccountRepositoryTest {

    private final InMemoryAccountRepository repository = new InMemoryAccountRepository();
    private final UUID orgA = UUID.randomUUID();
    private final UUID orgB = UUID.randomUUID();

    @Test
    void listsOnlyOwnAccountsSortedByNameIgnoringCaseThenId() {
        final var zeta = repository.save(account(1, orgA, "Zeta"));
        final var lateLower = repository.save(account(9, orgA, "alpha"));
        final var earlyUpper = repository.save(account(2, orgA, "Alpha"));
        final var lastLower = repository.save(account(1L << 40, orgA, "alpha"));
        final var firstLower = repository.save(account(3, orgA, "alpha"));
        final var aardvark = repository.save(account(4, orgB, "Aardvark"));
        final var otherZeta = repository.save(account(5, orgB, "zeta"));

        assertThat(repository.findByOrganizationId(orgA))
                .containsExactly(earlyUpper, firstLower, lateLower, lastLower, zeta);
        assertThat(repository.findByOrganizationId(orgB)).containsExactly(aardvark, otherZeta);
    }

    @Test
    void returnsEmptyListWhenOrganizationHasNoAccounts() {
        assertThat(repository.findByOrganizationId(UUID.randomUUID())).isNotNull().isEmpty();

        repository.save(account(1, orgA, "alpha"));

        assertThat(repository.findByOrganizationId(UUID.randomUUID())).isNotNull().isEmpty();
    }

    private static Account account(long id, UUID organizationId, String name) {
        return new Account(new UUID(0, id), organizationId, name, "EUR");
    }
}
