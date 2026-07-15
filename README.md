# kotlin-staking-rewards

**Time-based staking reward accrual in Kotlin** — the same protocol I built on
Solana (Anchor/Rust), EVM (Solidity), and Java, now idiomatic Kotlin:
`reward = staked · elapsed · rewardRate / 1e12`, with `claim` resetting the accrual clock.

`StakingPosition` (`src/main/kotlin/dev/liander/staking/`) uses `BigInteger` for the
intermediate product to avoid overflow, `require(...)` for input validation, and a
`companion object` for the pure `rewardFor` calculation. Tested with **kotlin.test on
JUnit 5**.

## Tests (`src/test/kotlin/...`)

- `claim` then `claim` accrues from the reset point (100, then 150),
- `pending` is read-only,
- reward is proportional to elapsed time,
- zero stake / zero time yields nothing,
- negative inputs and backwards time are rejected (`assertFailsWith`).

## Run it

```bash
mvn -B test
```

Requires JDK 17+ and Maven (the Kotlin compiler is pulled via `kotlin-maven-plugin`).
CI (`.github/workflows/ci.yml`) provisions Temurin 17 and runs `mvn -B test`.

## License

MIT — see [LICENSE](LICENSE).
