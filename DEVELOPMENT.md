# 🚀 Guia de Desenvolvimento - AgroColetor

## 📋 Índice

1. [Setup do Ambiente](#setup-do-ambiente)
2. [Build e Execução](#build-e-execução)
3. [Arquitetura Detalhada](#arquitetura-detalhada)
4. [MapLibre: Guia Completo](#maplibre-guia-completo)
5. [Troubleshooting](#troubleshooting)
6. [Boas Práticas](#boas-práticas)

---

## Setup do Ambiente

### Requisitos

- **Android Studio**: Hedgehog (2023.1.1) ou superior
- **JDK**: 17 (incluído no Android Studio)
- **Gradle**: 8.7 (wrapper incluído)
- **SDK Android**: 
  - Min SDK: 26 (Android 8.0)
  - Target SDK: 35 (Android 15)
  - Compile SDK: 35

### Instalação do Android Studio

1. Baixe do site oficial: https://developer.android.com/studio
2. Instale com os componentes:
   - Android SDK
   - Android SDK Platform
   - Android Virtual Device (AVD)

### Configuração Inicial

```bash
# Clone o repositório
git clone https://github.com/SauloRodrigues20/Agrogeocolector.git
cd Agrogeocolector

# Dê permissão ao Gradle Wrapper
chmod +x gradlew

# Sincronize dependências
./gradlew --refresh-dependencies
```

---

## Build e Execução

### Via Android Studio

1. **Abra o projeto**: File → Open → Selecione a pasta do projeto
2. **Aguarde Gradle Sync**: Verá "Gradle Sync" na barra inferior
3. **Configure emulador ou dispositivo**:
   - Emulador: Tools → Device Manager → Create Device
   - Dispositivo físico: Habilite "Depuração USB" nas opções de desenvolvedor
4. **Execute**: Shift + F10 ou clique no botão ▶️ "Run"

### Via Linha de Comando

```bash
# Build debug APK
./gradlew assembleDebug

# Instalar no dispositivo conectado
./gradlew installDebug

# Build release APK (assinado)
./gradlew assembleRelease

# Run tests
./gradlew test

# Run connected tests (instrumented)
./gradlew connectedAndroidTest
```

### APK gerado

```
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release.apk
```

---

## Arquitetura Detalhada

### Camadas da Aplicação

```
┌─────────────────────────────────────┐
│         Presentation Layer          │
│   (Compose UI + ViewModels)         │
├─────────────────────────────────────┤
│         Domain Layer                │
│   (UseCases + Business Logic)       │
├─────────────────────────────────────┤
│         Data Layer                  │
│   ├─ Local (Room Database)          │
│   ├─ Remote (Future: API)           │
│   └─ Sync (WorkManager)             │
└─────────────────────────────────────┘
```

### Fluxo de Dados (Unidirecional)

```
User Action → UI Event → ViewModel → UseCase → Repository → Data Source
                  ↑                                              ↓
                  └──────────────── State Update ───────────────┘
```

### Dependency Injection com Hilt

```kotlin
// Modules
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase
}

// Injection
@HiltAndroidApp
class AgroColetorApp : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity()

@HiltWorker
class SyncWorker @AssistedInject constructor(...)
```

---

## MapLibre: Guia Completo

### Por que MapLibre?

**Comparação com outras soluções:**

| Feature | MapLibre | Google Maps | Mapbox |
|---------|----------|-------------|--------|
| Custo | ✅ Grátis | ❌ $200/mês+ | ❌ $5/mês+ |
| Open Source | ✅ Sim | ❌ Não | ❌ Não |
| Offline | ✅ Nativo | ⚠️ Complexo | ⚠️ Complexo |
| Performance | ✅ OpenGL | ⚠️ Variável | ✅ OpenGL |

### Configuração Passo a Passo

#### 1. Adicionar Repositório Maven

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://maven.maplibre.org/releases/")
        }
    }
}
```

#### 2. Adicionar Dependência

```kotlin
// libs.versions.toml
[versions]
maplibre = "11.5.1"

[libraries]
maplibre-android = { group = "org.maplibre.gl", name = "android-sdk", version.ref = "maplibre" }
```

#### 3. Inicializar MapLibre

```kotlin
// Antes de criar MapView
MapLibre.getInstance(context)
```

#### 4. Criar Estilo do Mapa

**Opção 1: OpenStreetMap (Recomendado)**

```json
{
  "version": 8,
  "sources": {
    "osm": {
      "type": "raster",
      "tiles": ["https://tile.openstreetmap.org/{z}/{x}/{y}.png"],
      "tileSize": 256,
      "attribution": "© OpenStreetMap contributors"
    }
  },
  "layers": [{
    "id": "osm-tiles",
    "type": "raster",
    "source": "osm"
  }]
}
```

**Opção 2: Demotiles (Mais bonito, mas limitado)**

```kotlin
map.setStyle(Style.DEMOTILES)
```

**Opção 3: Custom Vector Tiles**

```kotlin
map.setStyle("https://seuservidor.com/style.json")
```

#### 5. Adicionar Camadas Customizadas

**Polígono (Fazenda):**

```kotlin
// Criar fonte GeoJSON
val polygon = Polygon.fromLngLats(listOf(
    listOf(
        Point.fromLngLat(lng1, lat1),
        Point.fromLngLat(lng2, lat2),
        // ...
    )
))
val source = GeoJsonSource("farm-source", FeatureCollection.fromFeature(
    Feature.fromGeometry(polygon)
))
style.addSource(source)

// Criar camada de preenchimento
val layer = FillLayer("farm-layer", "farm-source")
    .withProperties(
        fillColor("#88FF6B35"),
        fillOpacity(0.4f),
        fillOutlineColor("#FF6B35")
    )
style.addLayer(layer)
```

**Marcador (Amostra):**

```kotlin
val marker = SymbolLayer("marker-layer", "marker-source")
    .withProperties(
        iconImage("marker-icon"),
        iconSize(1.5f),
        iconAllowOverlap(true)
    )
```

#### 6. Habilitar Localização

```kotlin
@SuppressLint("MissingPermission")
fun enableLocation(map: MapLibreMap, style: Style, context: Context) {
    val locationComponent = map.locationComponent
    
    val options = LocationComponentActivationOptions
        .builder(context, style)
        .useDefaultLocationEngine(true)
        .build()
    
    locationComponent.activateLocationComponent(options)
    locationComponent.isLocationComponentEnabled = true
    locationComponent.cameraMode = CameraMode.TRACKING
    locationComponent.renderMode = RenderMode.COMPASS
}
```

#### 7. Listeners e Eventos

```kotlin
// Click no mapa
map.addOnMapClickListener { point ->
    Log.d("Map", "Clicked: ${point.latitude}, ${point.longitude}")
    true
}

// Long click
map.addOnMapLongClickListener { point ->
    // Adicionar marcador
    true
}

// Câmera movida
map.addOnCameraMoveListener {
    val position = map.cameraPosition
}
```

### Cache Offline de Tiles

Para funcionar offline, você precisa baixar os tiles antecipadamente:

```kotlin
// Definir região para download
val bounds = LatLngBounds.Builder()
    .include(LatLng(minLat, minLng))
    .include(LatLng(maxLat, maxLng))
    .build()

// Criar definição offline
val definition = OfflineTilePyramidRegionDefinition(
    styleURL,
    bounds,
    minZoom,
    maxZoom,
    context.resources.displayMetrics.density
)

// Baixar
offlineManager.createOfflineRegion(definition, metadata) { region ->
    region.setDownloadState(OfflineRegion.STATE_ACTIVE)
}
```

---

## Troubleshooting

### Problema: "Failed to resolve: org.maplibre.gl:android-sdk"

**Solução:**
```kotlin
// Verifique se o repositório Maven está adicionado
maven { url = uri("https://maven.maplibre.org/releases/") }
```

### Problema: "MapView não exibe nada (tela preta)"

**Possíveis causas:**
1. **Ciclo de vida não configurado**: Adicione `onStart()`, `onResume()`, etc.
2. **Estilo inválido**: Verifique JSON do estilo
3. **Permissões negadas**: Location precisa de permissão runtime

**Solução:**
```kotlin
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
```

### Problema: "Room cannot verify database schema"

**Solução:**
```kotlin
// Durante desenvolvimento, use:
.fallbackToDestructiveMigration()

// Em produção, crie migrations:
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE soil_samples ADD COLUMN altitude REAL")
    }
}
```

### Problema: "WorkManager não sincroniza"

**Verificações:**
1. Worker está anotado com `@HiltWorker`?
2. Constraint de rede está correto?
3. App tem permissão de internet?

**Debug:**
```kotlin
val workInfo = WorkManager.getInstance(context)
    .getWorkInfosForUniqueWork(SyncWorker.WORK_NAME)
    .get()

workInfo.forEach {
    Log.d("Sync", "State: ${it.state}")
    Log.d("Sync", "Run attempt: ${it.runAttemptCount}")
}
```

### Problema: "Foto fica muito grande no banco"

**NÃO FAÇA:**
```kotlin
❌ val bitmap = BitmapFactory.decodeFile(path)
❌ sample.photoBlob = bitmap.toByteArray()
```

**FAÇA:**
```kotlin
✅ val path = ImageFileUtils.saveAndCompressImage(context, uri)
✅ sample.photoPath = path
```

---

## Boas Práticas

### 1. Sempre use Coroutines para I/O

```kotlin
// ❌ Errado
fun loadSamples() {
    val samples = dao.getAllSamples() // Bloqueia UI thread
}

// ✅ Correto
viewModelScope.launch {
    dao.getAllSamples().collect { samples ->
        _state.value = samples
    }
}
```

### 2. Nunca exponha MutableStateFlow

```kotlin
// ❌ Errado
val state = MutableStateFlow<State>(State.Loading)

// ✅ Correto
private val _state = MutableStateFlow<State>(State.Loading)
val state: StateFlow<State> = _state.asStateFlow()
```

### 3. Use remember para objetos pesados no Compose

```kotlin
// ❌ Errado
@Composable
fun MapScreen() {
    val mapView = MapView(LocalContext.current) // Recria a cada recomposição
}

// ✅ Correto
@Composable
fun MapScreen() {
    val mapView = remember { MapView(LocalContext.current) }
}
```

### 4. Libere recursos no DisposableEffect

```kotlin
DisposableEffect(key) {
    val resource = allocate()
    onDispose {
        resource.release() // Sempre libere!
    }
}
```

### 5. Teste sincronização com DeviceOfflineConstraint

```kotlin
@Test
fun testSyncWorker() = runTest {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
    
    val request = OneTimeWorkRequestBuilder<SyncWorker>()
        .setConstraints(constraints)
        .build()
    
    val workManager = WorkManager.getInstance(context)
    workManager.enqueue(request).result.get()
    
    val workInfo = workManager.getWorkInfoById(request.id).get()
    assert(workInfo.state == WorkInfo.State.SUCCEEDED)
}
```

---

## 🔗 Links Úteis

- **MapLibre Docs**: https://maplibre.org/maplibre-native/android/
- **Room Guide**: https://developer.android.com/training/data-storage/room
- **Compose Docs**: https://developer.android.com/jetpack/compose
- **WorkManager**: https://developer.android.com/topic/libraries/architecture/workmanager
- **Hilt**: https://developer.android.com/training/dependency-injection/hilt-android

---

**Dúvidas?** Abra uma issue no GitHub!
