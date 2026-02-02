package com.compareprices.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.compareprices.data.local.ProductEntity
import kotlinx.coroutines.flow.Flow

@Composable
fun AddItemDialog(
  onDismiss: () -> Unit,
  searchProducts: (String) -> Flow<List<ProductEntity>>,
  onConfirm: (productId: Long, quantity: Double, unit: String) -> Unit
) {
  var showProductSearch by remember { mutableStateOf(false) }
  var selectedProduct by remember { mutableStateOf<ProductEntity?>(null) }
  var quantity by remember { mutableStateOf("1") }
  var unit by remember { mutableStateOf("unidades") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Text(
          text = "Agregar Producto",
          style = MaterialTheme.typography.titleLarge
        )

        // Product selection
        OutlinedButton(
          onClick = { showProductSearch = true },
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = selectedProduct?.name ?: "Seleccionar producto",
            modifier = Modifier.weight(1f)
          )
        }

        // Quantity input
        OutlinedTextField(
          value = quantity,
          onValueChange = { quantity = it },
          label = { Text("Cantidad") },
          modifier = Modifier.fillMaxWidth()
        )

        // Unit selector
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
          expanded = expanded,
          onExpandedChange = { expanded = !expanded }
        ) {
          OutlinedTextField(
            value = unit,
            onValueChange = {},
            readOnly = true,
            label = { Text("Unidad") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
          ) {
            listOf("unidades", "kg", "g", "litros", "ml", "paquete").forEach { option ->
              DropdownMenuItem(
                text = { Text(option) },
                onClick = {
                  unit = option
                  expanded = false
                }
              )
            }
          }
        }

        // Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = onDismiss) {
            Text("Cancelar")
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              selectedProduct?.let { product ->
                val qty = quantity.toDoubleOrNull() ?: 1.0
                onConfirm(product.id, qty, unit)
                onDismiss()
              }
            },
            enabled = selectedProduct != null
          ) {
            Text("Agregar")
          }
        }
      }
    }
  }

  if (showProductSearch) {
    ProductSearchDialog(
      searchProducts = searchProducts,
      onDismiss = { showProductSearch = false },
      onSelect = { product ->
        selectedProduct = product
        showProductSearch = false
      }
    )
  }
}

@Composable
fun ProductSearchDialog(
  searchProducts: (String) -> Flow<List<ProductEntity>>,
  onDismiss: () -> Unit,
  onSelect: (ProductEntity) -> Unit
) {
  var query by remember { mutableStateOf("") }
  val products by searchProducts(query).collectAsStateWithLifecycle(initialValue = emptyList())

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(500.dp)
        .padding(16.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Buscar Producto",
            style = MaterialTheme.typography.titleLarge
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, "Cerrar")
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = query,
          onValueChange = { query = it },
          label = { Text("Buscar") },
          leadingIcon = { Icon(Icons.Default.Search, null) },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (products.isEmpty() && query.isNotBlank()) {
          Text(
            text = "No se encontraron productos",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        } else {
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(products) { product ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { onSelect(product) }
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium
                  )
                  product.brand?.let { brand ->
                    Text(
                      text = brand,
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
