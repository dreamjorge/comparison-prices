package com.compareprices.ui.premium

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
  onDismiss: () -> Unit,
  viewModel: PremiumViewModel = hiltViewModel()
) {
  val isPremium by viewModel.isPremium.collectAsState()

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
      
      if (isPremium) {
        Text(
          text = "¡Ya eres un usuario PRO!",
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )
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
