package dev.sandbox.ledger.application;

import dev.sandbox.ledger.application.port.in.CanPayUseCase;
import dev.sandbox.ledger.application.port.in.GetBalanceUseCase;
import dev.sandbox.ledger.application.port.out.AccountRepository;
import dev.sandbox.ledger.domain.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.UUID;

public class CanPayService implements CanPayUseCase {

    private final AccountRepository accountRepository;
    private final GetBalanceUseCase getBalance;

    public CanPayService(AccountRepository accountRepository, GetBalanceUseCase getBalance) {
        this.accountRepository = accountRepository;
        this.getBalance = getBalance;
    }

    @Override
    public boolean canPay(UUID organizationId, UUID accountId, BigDecimal amount, String currency) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        final var account = accountRepository
                .findByIdAndOrganizationId(accountId, organizationId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        if (!account.supports(currency)) {
            return false;
        }
        return getBalance.balanceOf(organizationId, accountId).compareTo(amount) >= 0;
    }
}
