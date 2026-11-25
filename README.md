# factory-sandbox

A small payments ledger used as a sandbox for pull request review bot tests. It is not production code.

## What it does

Accounts belong to an organization. Credits and debits are posted to an account as ledger entries,
and the balance of an account is the sum of its entries. A debit above the balance is refused.

## Use cases

- Post an entry: `PostEntryUseCase`
- Balance: `GetBalanceUseCase`
- Account statement: `GetAccountStatementUseCase`
- Export account statement as text: `ExportAccountStatementUseCase`

## Layout

- `domain`: records and exceptions, no dependencies
- `application/port/in`: use case interfaces
- `application/port/out`: repository interfaces
- `application`: use case implementations
- `infra`: in-memory repositories

## Build

Requires Java 25 and Maven.

```bash
mvn -B -q test
```

## Running the tests

The tests need JDK 25 and run with:

```bash
mvn -q -B test
```

The first run downloads the dependencies from Maven Central.

The rules a reviewer checks are in [REVIEW_RULES.md](REVIEW_RULES.md).
