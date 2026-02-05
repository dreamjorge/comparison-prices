package com.compareprices.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

private const val REWARDED_TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

@Composable
fun RewardedAdButton(
  buttonText: String,
  onRewardEarned: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activity = context.findActivity()
  var rewardedAd by remember { mutableStateOf<RewardedAd?>(null) }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val loadAd = fun loadAd() {
    if (isLoading) {
      return
    }
    isLoading = true
    errorMessage = null
    RewardedAd.load(
      context,
      REWARDED_TEST_AD_UNIT_ID,
      AdRequest.Builder().build(),
      object : RewardedAdLoadCallback() {
        override fun onAdLoaded(ad: RewardedAd) {
          rewardedAd = ad
          isLoading = false
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
          rewardedAd = null
          isLoading = false
          errorMessage = "No se pudo cargar el anuncio. Intenta nuevamente."
        }
      }
    )
  }

  LaunchedEffect(Unit) {
    loadAd()
  }

  Column(modifier = modifier) {
    Button(
      onClick = {
        val currentAd = rewardedAd
        if (currentAd != null && activity != null) {
          currentAd.show(activity) {
            onRewardEarned()
            loadAd()
          }
          rewardedAd = null
        } else {
          errorMessage = "El anuncio aún no está listo."
          loadAd()
        }
      },
      enabled = !isLoading,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(buttonText)
    }

    if (errorMessage != null) {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = errorMessage.orEmpty(),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error
      )
    }
  }
}
