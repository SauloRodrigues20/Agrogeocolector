package com.agrogeocolector.ui.samples

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agrogeocolector.data.local.entity.SoilSample
import java.text.SimpleDateFormat
import java.util.*

/**
 * Tela de exemplo para listar as amostras de solo.
 * 
 * Demonstra:
 * - Integração com ViewModel + Hilt
 * - Uso de Flow/StateFlow no Compose
 * - LazyColumn para listas
 * - Material 3 components
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamplesListScreen(
    viewModel: SamplesViewModel = hiltViewModel(),
    onSampleClick: (SoilSample) -> Unit = {}
) {
    val samples by viewModel.samples.collectAsState()
    val unsyncedCount by viewModel.unsyncedCount.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Amostras Coletadas") },
                actions = {
                    // Badge com contador não sincronizado
                    if (unsyncedCount > 0) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error
                        ) {
                            Text("$unsyncedCount")
                        }
                    }
                    
                    IconButton(onClick = {
                        // TODO: Trigger sync
                    }) {
                        Icon(Icons.Default.Sync, contentDescription = "Sincronizar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is SamplesViewModel.UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                is SamplesViewModel.UiState.Error -> {
                    Text(
                        text = (uiState as SamplesViewModel.UiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                is SamplesViewModel.UiState.Success -> {
                    if (samples.isEmpty()) {
                        // Empty state
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Nenhuma amostra coletada",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Adicione uma amostra no mapa",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // List of samples
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = samples,
                                key = { it.id }
                            ) { sample ->
                                SampleCard(
                                    sample = sample,
                                    onClick = { onSampleClick(sample) },
                                    onDelete = { viewModel.deleteSample(sample) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SampleCard(
    sample: SoilSample,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Data/Hora
                Text(
                    text = formatTimestamp(sample.timestamp),
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Coordenadas
                Text(
                    text = "Lat: ${String.format("%.6f", sample.latitude)}, " +
                           "Lng: ${String.format("%.6f", sample.longitude)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Observação (se houver)
                if (sample.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = sample.note,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )
                }
                
                // Status de sincronização
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (sample.isSynced) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        }
                    ) {
                        Text(
                            text = if (sample.isSynced) "Sincronizado" else "Pendente",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (sample.isSynced) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            }
                        )
                    }
                    
                    if (sample.photoPath != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "📷 Foto",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
            
            // Botão deletar
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Deletar amostra",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
