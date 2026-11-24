package dev.sandbox.ledger.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.sandbox.ledger.domain.Account;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ListAccountsServiceTest {

    private final LedgerFixture fixture = new LedgerFixture();
    private final ListAccountsService listAccounts = new ListAccountsService(fixture.accounts);
    private final UUID orgA = UUID.randomUUID();
    private final UUID orgB = UUID.randomUUID();

    @Test
    void listsOnlyTheAccountsOfTheOrganizationInRepositoryOrder() {
        save(orgA, "b");
        save(orgA, "A");
        final var other = save(orgB, "A");

        final var accounts = listAccounts.accountsOf(orgA);

        assertThat(accounts).extracting(Account::name).containsExactly("A", "b");
        assertThat(accounts).doesNotContain(other);
        assertThat(accounts).isEqualTo(fixture.accounts.findByOrganizationId(orgA));
    }

    @Test
    void returnsAnEmptyListForAnOrganizationWithoutAccounts() {
        save(orgA, "a");

        assertThat(listAccounts.accountsOf(UUID.randomUUID())).isNotNull().isEmpty();
    }

    @Test
    void rejectsANullOrganizationId() {
        assertThatThrownBy(() -> listAccounts.accountsOf(null)).isInstanceOf(NullPointerException.class);
    }

    private Account save(UUID organizationId, String name) {
        return fixture.accounts.save(new Account(UUID.randomUUID(), organizationId, name, "BRL"));
    }
}
