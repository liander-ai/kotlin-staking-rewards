package dev.liander.staking

import java.math.BigInteger

/**
 * Time-based staking reward accrual in Kotlin — the same protocol I built on
 * Solana (Anchor), EVM (Solidity) and the JVM in Java:
 * `reward = staked * elapsed * rewardRate / 1e12`, with `claim` resetting the clock.
 *
 * Uses [BigInteger] for the intermediate product to avoid overflow. Pure and
 * deterministic, so it unit-tests cleanly with kotlin.test on JUnit 5.
 */
class StakingPosition(
    private val staked: Long,
    private val rewardRate: Long,
    startTime: Long,
) {
    private var lastTime: Long = startTime
    private var accrued: Long = 0

    init {
        require(staked >= 0 && rewardRate >= 0) { "staked and rewardRate must be non-negative" }
    }

    /** Unclaimed rewards as of [now], without mutating state. */
    fun pending(now: Long): Long = accrued + rewardFor(staked, elapsedSince(now), rewardRate)

    /** Settle accrued rewards up to [now], resetting the clock, and return them. */
    fun claim(now: Long): Long {
        accrued += rewardFor(staked, elapsedSince(now), rewardRate)
        lastTime = now
        val payout = accrued
        accrued = 0
        return payout
    }

    private fun elapsedSince(now: Long): Long {
        require(now >= lastTime) { "time must not go backwards" }
        return now - lastTime
    }

    companion object {
        /** Fixed-point scale (1e12), matching the Solana/EVM implementations. */
        const val SCALE: Long = 1_000_000_000_000L

        /** Reward accrued by [staked] tokens over [elapsed] seconds at [rate]. */
        fun rewardFor(staked: Long, elapsed: Long, rate: Long): Long {
            require(staked >= 0 && elapsed >= 0 && rate >= 0) { "inputs must be non-negative" }
            return BigInteger.valueOf(staked)
                .multiply(BigInteger.valueOf(elapsed))
                .multiply(BigInteger.valueOf(rate))
                .divide(BigInteger.valueOf(SCALE))
                .longValueExact()
        }
    }
}
