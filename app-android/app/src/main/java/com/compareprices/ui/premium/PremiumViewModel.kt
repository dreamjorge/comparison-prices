package com.compareprices.ui.premium

import androidx.lifecycle.ViewModel
import com.compareprices.data.local.UserPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    val userPrefs: UserPrefs
) : ViewModel()
