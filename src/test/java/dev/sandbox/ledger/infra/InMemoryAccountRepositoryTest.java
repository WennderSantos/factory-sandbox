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
        final var beta = repository.save(account(1, orgA, "beta"));
        final var upper = repository.save(account(2, orgA, "Alpha"));
        final var lower = repository.save(account(3, orgA, "alpha"));
        final var aardvark = repository.save(account(4, orgB, "Aardvark"));
        final var otherBeta = repository.save(account(5, orgB, "beta"));

        assertThat(repository.findByOrganizationId(orgA)).containsExactly(upper, lower, beta);
        assertThat(repository.findByOrganizationId(orgB)).containsExactly(aardvark, otherBeta);
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
