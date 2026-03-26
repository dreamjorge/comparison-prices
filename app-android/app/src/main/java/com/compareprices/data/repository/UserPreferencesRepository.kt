package com.compareprices.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
  @ApplicationContext private val context: Context
) {
  private val isPremiumKey = booleanPreferencesKey("is_premium")
  private val rewardedUnlockUntilKey = longPreferencesKey("rewarded_unlock_until")
  private val lastCheapestStoreKey = stringPreferencesKey("last_cheapest_store")

  val premiumStatus: Flow<PremiumStatus> = context.dataStore.data
    .map { preferences ->
      PremiumStatus(
        isPaid = preferences[isPremiumKey] ?: false,
        rewardedUntilMillis = preferences[rewardedUnlockUntilKey] ?: 0L
      )
    }

  val isPremium: Flow<Boolean> = premiumStatus
    .map { status ->
      status.hasAccess(System.currentTimeMillis())
    }

  suspend fun setPremium(isPremium: Boolean) {
    context.dataStore.edit { preferences ->
      preferences[isPremiumKey] = isPremium
    }
  }

  suspend fun unlockRewardedAccess(durationMillis: Long, nowMillis: Long = System.currentTimeMillis()) {
    val newExpiry = nowMillis + durationMillis
    context.dataStore.edit { preferences ->
      val currentExpiry = preferences[rewardedUnlockUntilKey] ?: 0L
      preferences[rewardedUnlockUntilKey] = maxOf(currentExpiry, newExpiry)
    }
  }

  fun getLastCheapestStore(): Flow<String?> =
    context.dataStore.data.map { preferences -> preferences[lastCheapestStoreKey] }

  suspend fun setLastCheapestStore(name: String) {
    context.dataStore.edit { preferences ->
      preferences[lastCheapestStoreKey] = name
    }
  }

  suspend fun clearExpiredRewards(nowMillis: Long = System.currentTimeMillis()) {
    context.dataStore.edit { preferences ->
      val expiry = preferences[rewardedUnlockUntilKey] ?: 0L
      if (expiry <= nowMillis) {
        preferences.remove(rewardedUnlockUntilKey)
      }
    }
  }
}

data class PremiumStatus(
  val isPaid: Boolean,
  val rewardedUntilMillis: Long
) {
  fun hasAccess(nowMillis: Long): Boolean = isPaid || rewardedUntilMillis > nowMillis

  fun isRewardedActive(nowMillis: Long): Boolean = !isPaid && rewardedUntilMillis > nowMillis
}
