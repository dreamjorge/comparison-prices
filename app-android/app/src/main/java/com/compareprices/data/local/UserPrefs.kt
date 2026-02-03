package com.compareprices.data.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var isProUser: Boolean
        get() = prefs.getBoolean("is_pro", false)
        set(value) = prefs.edit { putBoolean("is_pro", value) }

    var rewardsCount: Int
        get() = prefs.getInt("rewards_count", 0)
        set(value) = prefs.edit { putInt("rewards_count", value) }

    fun unlockProTemporarily() {
        // Mocking temporary unlock for demo
        isProUser = true
    }
}
