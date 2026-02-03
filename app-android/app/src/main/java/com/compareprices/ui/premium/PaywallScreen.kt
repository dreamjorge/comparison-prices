package com.compareprices.ui.premium

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.compareprices.ui.components.RewardedAdButton
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
  onDismiss: () -> Unit,
  viewModel: PremiumViewModel = hiltViewModel()
) {
  val uiState by viewModel.premiumUiState.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("ComparePrices Pro") },
        navigationIcon = {
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar")
          }
        }
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = Icons.Default.Star,
        contentDescription = null,
        modifier = Modifier.size(80.dp),
        tint = MaterialTheme.colorScheme.primary
      )
      
      Spacer(modifier = Modifier.height(24.dp))
      
      Text(
        text = "Desbloquea todo el potencial",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
      )
      
      Spacer(modifier = Modifier.height(32.dp))
      
      BenefitItem("Sin anuncios", "Navega sin interrupciones.")
      BenefitItem("Historial extendido", "Mira precios de los últimos 6 meses.")
      BenefitItem("Alertas priorizadas", "Entérate primero de las ofertas.")
      
      Spacer(modifier = Modifier.weight(1f))
      
      if (uiState.hasAccess) {
        Text(
          text = if (uiState.isPaid) "¡Ya eres un usuario PRO!" else "Pro temporal activo",
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )
        if (!uiState.isPaid && uiState.rewardedUntilMillis > 0L) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Disponible hasta ${formatRewardedUntil(uiState.rewardedUntilMillis)}",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
          )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Volver")
        }
      } else {
        Button(
          onClick = { viewModel.purchasePremium() },
          modifier = Modifier.fillMaxWidth(),
          shape = MaterialTheme.shapes.medium
        ) {
          Text("Obtener Pro por $2.99 USD", fontSize = 18.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        RewardedAdButton(
          buttonText = "Ver anuncio para 24h de Pro",
          onRewardEarned = { viewModel.unlockRewardedAccess() },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onDismiss) {
          Text("Quizás más tarde", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    }
  }
}

@Composable
private fun BenefitItem(title: String, description: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = Icons.Default.Check,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.width(16.dp))
    Column {
      Text(text = title, fontWeight = FontWeight.Bold)
      Text(text = description, style = MaterialTheme.typography.bodyMedium)
    }
  }
}

private fun formatRewardedUntil(timestampMillis: Long): String {
  val formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
  return formatter.format(Date(timestampMillis))
}
