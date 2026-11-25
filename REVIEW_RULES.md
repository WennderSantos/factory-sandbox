# Review rules

Every pull request is checked against these rules. A violation of rules 1 to 5 blocks the merge.

1. Dependencies point inward: `infra -> application -> domain`. The domain imports nothing from the other packages.
2. Callers depend on input ports (`application/port/in`), never on a concrete service or a repository.
3. Every repository method takes an `organizationId` and filters by it. A lookup by id alone is not allowed.
4. Money is `BigDecimal` with scale 2. Never `double` or `float`, not even for intermediate results.
5. Rounding is explicit: always pass a `RoundingMode`.
6. Domain types are immutable records that validate their own invariants.
7. Constructor injection only, with `private final` fields.
8. Tests use JUnit 5 and AssertJ. The `assert` keyword is not allowed.
9. No `Thread.sleep` in tests. Use a fixed `Clock` when time matters.
10. Every new behaviour has a test, including the case of another organization asking for the data.
11. No unused imports, no commented out code.
12. Comments explain why, not what.
13. Pull requests stay small and do one thing.
14. **LEDGER-14 (must)** A use case never returns a collection its caller can change: return `List.copyOf(...)` or another unmodifiable list.
