package com.agrogeocolector.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.PropertyFactory.*
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Polygon

/**
 * Tela principal do mapa usando MapLibre Native (Open Source).
 * 
 * Features:
 * - Mapa gratuito do OpenStreetMap
 * - Localização em tempo real (bolinha azul)
 * - Overlay de polígono da fazenda (GeoJSON)
 * - 100% Offline-capable
 */
@SuppressLint("MissingPermission")
@Composable
fun MapLibreScreen(
    modifier: Modifier = Modifier,
    onAddSampleClick: (LatLng) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    
    // Inicializa MapLibre (necessário antes de criar MapView)
    DisposableEffect(context) {
        MapLibre.getInstance(context)
        onDispose { }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // MapView integrado ao Compose via AndroidView
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    mapView = this
                    
                    // Obtém o mapa de forma assíncrona
                    getMapAsync { map ->
                        mapLibreMap = map
                        
                        // Configura o estilo do mapa (OpenStreetMap)
                        map.setStyle(getOSMStyle()) { style ->
                            // Adiciona overlay da fazenda (polígono de exemplo)
                            addFarmOverlay(style)
                            
                            // Ativa o componente de localização
                            enableLocationComponent(map, style, ctx)
                            
                            // Centraliza na localização atual
                            getCurrentLocation(ctx) { location ->
                                currentLocation = location
                                map.cameraPosition = CameraPosition.Builder()
                                    .target(location)
                                    .zoom(15.0)
                                    .build()
                            }
                        }
                        
                        // Listener para cliques no mapa
                        map.addOnMapClickListener { point ->
                            onAddSampleClick(point)
                            true
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Botão FAB para adicionar amostra na localização atual
        FloatingActionButton(
            onClick = {
                currentLocation?.let { onAddSampleClick(it) }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar amostra")
        }
        
        // Botão para centralizar no usuário
        FloatingActionButton(
            onClick = {
                currentLocation?.let { location ->
                    mapLibreMap?.animateCamera(
                        org.maplibre.android.camera.CameraUpdateFactory.newLatLngZoom(
                            location, 17.0
                        )
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Minha localização")
        }
    }
    
    // Gerencia o ciclo de vida do MapView
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView?.onStart()
                Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                Lifecycle.Event.ON_STOP -> mapView?.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView?.onDestroy()
                else -> {}
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView?.onDestroy()
        }
    }
}

/**
 * Retorna o estilo do mapa usando OpenStreetMap.
 * Esta URL aponta para os tiles gratuitos do OSM.
 */
private fun getOSMStyle(): String {
    // Estilo básico do OSM em formato MapLibre
    return """
    {
      "version": 8,
      "sources": {
        "osm": {
          "type": "raster",
          "tiles": [
            "https://tile.openstreetmap.org/{z}/{x}/{y}.png"
          ],
          "tileSize": 256,
          "attribution": "© OpenStreetMap contributors",
          "maxzoom": 19
        }
      },
      "layers": [
        {
          "id": "osm-tiles",
          "type": "raster",
          "source": "osm",
          "minzoom": 0,
          "maxzoom": 22
        }
      ]
    }
    """.trimIndent()
}

/**
 * Adiciona um polígono de exemplo representando os limites da fazenda.
 * Em produção, isso viria de um arquivo GeoJSON local ou do banco de dados.
 */
private fun addFarmOverlay(style: Style) {
    // Coordenadas de exemplo de um talhão (polígono)
    // Em produção, carregar de arquivo local ou banco de dados
    val farmBoundaryCoordinates = listOf(
        listOf(
            org.maplibre.geojson.Point.fromLngLat(-47.8825, -15.7940),
            org.maplibre.geojson.Point.fromLngLat(-47.8815, -15.7940),
            org.maplibre.geojson.Point.fromLngLat(-47.8815, -15.7950),
            org.maplibre.geojson.Point.fromLngLat(-47.8825, -15.7950),
            org.maplibre.geojson.Point.fromLngLat(-47.8825, -15.7940)
        )
    )
    
    val polygon = Polygon.fromLngLats(farmBoundaryCoordinates)
    val featureCollection = FeatureCollection.fromFeatures(listOf(
        org.maplibre.geojson.Feature.fromGeometry(polygon)
    ))
    
    // Adiciona a fonte GeoJSON
    val geoJsonSource = GeoJsonSource("farm-boundary-source", featureCollection)
    style.addSource(geoJsonSource)
    
    // Adiciona a camada de preenchimento (polígono semitransparente)
    val fillLayer = FillLayer("farm-boundary-layer", "farm-boundary-source")
        .withProperties(
            fillColor(Color.parseColor("#88FF6B35")), // Laranja semitransparente
            fillOpacity(0.4f),
            fillOutlineColor(Color.parseColor("#FF6B35"))
        )
    
    style.addLayer(fillLayer)
}

/**
 * Ativa o componente de localização do MapLibre.
 * Mostra a "bolinha azul" com a posição do usuário.
 */
@SuppressLint("MissingPermission")
private fun enableLocationComponent(map: MapLibreMap, style: Style, context: Context) {
    val locationComponent = map.locationComponent
    
    val activationOptions = LocationComponentActivationOptions.builder(context, style)
        .useDefaultLocationEngine(true)
        .build()
    
    locationComponent.activateLocationComponent(activationOptions)
    locationComponent.isLocationComponentEnabled = true
    locationComponent.cameraMode = CameraMode.TRACKING
    locationComponent.renderMode = RenderMode.COMPASS
}

/**
 * Obtém a localização atual do dispositivo usando FusedLocationProvider.
 * Funciona offline se o GPS estiver ativo.
 */
@SuppressLint("MissingPermission")
private fun getCurrentLocation(context: Context, onLocationReceived: (LatLng) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    
    // Tenta obter a última localização conhecida
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            onLocationReceived(LatLng(it.latitude, it.longitude))
        } ?: run {
            // Se não houver última localização, solicita uma nova
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { newLocation ->
                newLocation?.let {
                    onLocationReceived(LatLng(it.latitude, it.longitude))
                }
            }
        }
    }
}
