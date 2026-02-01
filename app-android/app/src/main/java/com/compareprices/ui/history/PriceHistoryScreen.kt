package com.compareprices.ui.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compareprices.data.local.PriceSnapshotDao
import com.compareprices.data.local.PriceSnapshotEntity
import com.compareprices.data.local.ProductDao
import com.compareprices.data.local.ProductEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.text.NumberFormat
import java.util.Locale

@HiltViewModel
class PriceHistoryViewModel @Inject constructor(
    private val productDao: ProductDao,
    private val priceSnapshotDao: PriceSnapshotDao
) : ViewModel() {
    private val _uiState = MutableStateFlow(PriceHistoryUiState())
    val uiState: StateFlow<PriceHistoryUiState> = _uiState

    fun loadHistory(productId: Long) {
        viewModelScope.launch {
            val snapshots = priceSnapshotDao.historyForProduct(productId)
            // For demo, if no real snapshots, we'll keep empty or seed some
            _uiState.value = PriceHistoryUiState(snapshots = snapshots)
        }
    }
}

data class PriceHistoryUiState(
    val snapshots: List<PriceSnapshotEntity> = emptyList()
)

@Composable
fun PriceHistoryScreen(
    productId: Long,
    productName: String,
    viewModel: PriceHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadHistory(productId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Historial: $productName",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.snapshots.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                Text(
                    text = "No hay datos históricos para este producto.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            PriceChart(snapshots = uiState.snapshots)
            Spacer(modifier = Modifier.height(24.dp))
            PriceList(snapshots = uiState.snapshots)
        }
    }
}

@Composable
fun PriceChart(snapshots: List<PriceSnapshotEntity>) {
    val prices = snapshots.sortedBy { it.capturedAt }.map { it.price.toFloat() }
    if (prices.size < 2) {
        Text("No hay suficientes datos para graficar.")
        return
    }

    val maxPrice = prices.maxOrNull() ?: 0f
    val minPrice = prices.minOrNull() ?: 0f
    val range = (maxPrice - minPrice).coerceAtLeast(1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        val lineColor = MaterialTheme.colorScheme.primary
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val width = size.width
            val height = size.height
            val stepX = width / (prices.size - 1)

            val path = Path()
            prices.forEachIndexed { index, price ->
                val x = index * stepX
                val y = height - ((price - minPrice) / range * height)
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            drawPath(path = path, color = lineColor, style = Stroke(width = 4.dp.toPx()))
        }
    }
}

@Composable
fun PriceList(snapshots: List<PriceSnapshotEntity>) {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    snapshots.sortedByDescending { it.capturedAt }.forEach { snapshot ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Captured: ${snapshot.capturedAt}", style = MaterialTheme.typography.bodySmall)
            Text(text = formatter.format(snapshot.price), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}
