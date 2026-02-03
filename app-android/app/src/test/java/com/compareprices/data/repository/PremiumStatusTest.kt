package com.compareprices.data.repository

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PremiumStatusTest {

  @Test
  fun `paid users always have access`() {
    val status = PremiumStatus(isPaid = true, rewardedUntilMillis = 0L)

    assertTrue(status.hasAccess(nowMillis = 0L))
    assertTrue(status.hasAccess(nowMillis = Long.MAX_VALUE))
    assertFalse(status.isRewardedActive(nowMillis = 0L))
  }

  @Test
  fun `rewarded access only valid before expiry`() {
    val status = PremiumStatus(isPaid = false, rewardedUntilMillis = 1_000L)

    assertTrue(status.hasAccess(nowMillis = 999L))
    assertTrue(status.isRewardedActive(nowMillis = 999L))
    assertFalse(status.hasAccess(nowMillis = 1_000L))
    assertFalse(status.isRewardedActive(nowMillis = 1_000L))
  }
}
