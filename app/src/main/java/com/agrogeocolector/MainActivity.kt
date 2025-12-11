package com.agrogeocolector

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agrogeocolector.ui.map.MapLibreScreen
import com.agrogeocolector.ui.theme.AgroColetorTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal do AgroColetor.
 * 
 * Features:
 * - Solicita permissões de localização e câmera
 * - Exibe o mapa com MapLibre
 * - Integrado com Hilt para DI
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            AgroColetorTheme {
                // Gerencia permissões necessárias
                val permissionsState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA
                    )
                )
                
                LaunchedEffect(Unit) {
                    if (!permissionsState.allPermissionsGranted) {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                }
                
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("AgroColetor") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                ) { paddingValues ->
                    if (permissionsState.allPermissionsGranted) {
                        MapLibreScreen(
                            modifier = Modifier.padding(paddingValues),
                            onAddSampleClick = { latLng ->
                                // TODO: Navegar para tela de adicionar amostra
                                // Por enquanto, apenas log
                                println("Adicionar amostra em: ${latLng.latitude}, ${latLng.longitude}")
                            }
                        )
                    } else {
                        // Tela de permissões negadas
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Permissões Necessárias",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    text = "Este app precisa de acesso à localização e câmera para funcionar.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Button(
                                    onClick = { permissionsState.launchMultiplePermissionRequest() }
                                ) {
                                    Text("Conceder Permissões")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
