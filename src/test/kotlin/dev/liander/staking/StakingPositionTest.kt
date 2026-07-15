package dev.liander.staking

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StakingPositionTest {

    // staked=1000, rate=1e9 => reward = 1000 * elapsed * 1e9 / 1e12 = elapsed
    private val staked = 1000L
    private val rate = 1_000_000_000L

    @Test
    fun claimThenClaimAccruesFromReset() {
        val pos = StakingPosition(staked, rate, 0L)
        assertEquals(100L, pos.claim(100L))
        assertEquals(150L, pos.claim(250L))
    }

    @Test
    fun pendingIsNonMutating() {
        val pos = StakingPosition(staked, rate, 0L)
        assertEquals(100L, pos.pending(100L))
        assertEquals(100L, pos.pending(100L))
        assertEquals(100L, pos.claim(100L))
    }

    @Test
    fun rewardIsProportionalToTime() {
        assertEquals(50L, StakingPosition.rewardFor(staked, 50L, rate))
        assertEquals(100L, StakingPosition.rewardFor(staked, 100L, rate))
    }

    @Test
    fun zeroStakeOrZeroTimeYieldsNothing() {
        assertEquals(0L, StakingPosition.rewardFor(0L, 100L, rate))
        assertEquals(0L, StakingPosition.rewardFor(staked, 0L, rate))
    }

    @Test
    fun rejectsNegativeInputs() {
        assertFailsWith<IllegalArgumentException> { StakingPosition(-1L, rate, 0L) }
    }

    @Test
    fun rejectsTimeGoingBackwards() {
        val pos = StakingPosition(staked, rate, 100L)
        assertFailsWith<IllegalArgumentException> { pos.claim(50L) }
    }
}
