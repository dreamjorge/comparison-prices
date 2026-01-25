package com.compareprices

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.compareprices.ui.AppRoot
import com.compareprices.ui.theme.ComparePricesTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      ComparePricesTheme {
        AppRoot()
      }
    }
  }
}