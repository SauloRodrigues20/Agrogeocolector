# 🌱 AgroColetor

**Aplicativo profissional de Agronomia para coleta de solo - 100% Gratuito e Offline-First**

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin)](https://kotlinlang.org/)
[![MapLibre](https://img.shields.io/badge/Maps-MapLibre-396CB2)](https://maplibre.org/)
[![Supabase](https://img.shields.io/badge/Backend-Supabase-3ECF8E?logo=supabase)](https://supabase.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> **🚀 Pronto para usar!** Clone, configure e execute em 15 minutos - veja o [QUICK_START.md](QUICK_START.md)

## 📋 Sobre o Projeto

O **AgroColetor** é uma solução Open Source para agrônomos coletarem amostras de solo diretamente no campo, sem depender de conexão com internet ou APIs pagas. Todas as funcionalidades essenciais funcionam offline, e os dados são sincronizados automaticamente quando a internet estiver disponível.

### ✨ Características Principais

- 🗺️ **Mapas Gratuitos**: MapLibre Native + OpenStreetMap (sem custos de API)
- 📡 **Offline-First**: Funciona completamente sem internet
- 📸 **Otimização de Fotos**: Compressão inteligente (JPEG 80%, max 1920px)
- 💾 **Armazenamento Local**: Room Database (SQLite)
- 🔄 **Sincronização Inteligente**: WorkManager com retry automático
- 📍 **GPS de Alta Precisão**: FusedLocationProvider
- 🎨 **UI Moderna**: Jetpack Compose + Material 3

## 🏗️ Arquitetura

### Stack Tecnológica

| Componente | Tecnologia | Motivo |
|------------|-----------|--------|
| **Linguagem** | Kotlin 2.0 | Modern Android Development |
| **UI** | Jetpack Compose | Declarativo e reativo |
| **Mapas** | MapLibre Native | Open Source, sem custos |
| **Tiles** | OpenStreetMap | Gratuito e colaborativo |
| **Banco de Dados** | Room (SQLite) | Offline-first |
| **Localização** | FusedLocationProvider | Precisão e economia de bateria |
| **Câmera** | CameraX | API moderna unificada |
| **Imagens** | Coil | Carregamento eficiente |
| **DI** | Hilt | Injeção de dependências |
| **Background** | WorkManager | Sincronização confiável || **Backend** | Supabase | PostgreSQL + Storage |
### Estrutura do Projeto

```
app/src/main/java/com/agrogeocolector/
├── data/
│   ├── local/
│   │   ├── entity/
│   │   │   └── SoilSample.kt          # Entidade Room
│   │   ├── dao/
│   │   │   └── SoilSampleDao.kt       # DAO com Flow
│   │   └── AppDatabase.kt              # Banco Room
│   └── sync/
│       ├── SyncWorker.kt               # Worker de sincronização
│       └── SyncManager.kt              # Gerenciador de sync
├── di/
│   └── DatabaseModule.kt               # Módulos Hilt
├── ui/
│   ├── map/
│   │   └── MapLibreScreen.kt          # Tela do mapa
│   └── theme/
│       └── Theme.kt                    # Material 3 Theme
├── util/
│   └── ImageFileUtils.kt              # Otimização de fotos
├── AgroColetorApp.kt                  # Application class
└── MainActivity.kt                    # Activity principal
```

## 🚀 Como Começar

> **📱 Quer só instalar no celular? [INSTALACAO_APK.md](INSTALACAO_APK.md) - Baixe e instale como um app normal!**
> 
> **⚡ Quer testar/modificar o código? [COMO_USAR.md](COMO_USAR.md) - Guia completo com Android Studio!**
> 
> **🎯 Desenvolvedor experiente? [QUICK_START.md](QUICK_START.md) - Setup técnico em 15 minutos!**

### Pré-requisitos

- ✅ **Android Studio** Hedgehog ou superior
- ✅ **JDK 17** (incluído no Android Studio)
- ✅ **Conta no Supabase** (gratuita - https://supabase.com)
- ✅ **Dispositivo Android** API 26+ com GPS e câmera

### Instalação Rápida

**1. Clone o repositório**
```bash
git clone https://github.com/SauloRodrigues20/Agrogeocolector.git
cd Agrogeocolector
```

**2. Configure o Supabase**

Copie o arquivo de exemplo:
```bash
cp local.properties.example local.properties
```

Edite `local.properties` e adicione suas credenciais:
```properties
SUPABASE_URL=https://seu-projeto.supabase.co
SUPABASE_ANON_KEY=sua-chave-anon-aqui
```

📖 **Guia completo**: [SUPABASE_SETUP.md](SUPABASE_SETUP.md)

**3. Abra no Android Studio**
- File → Open → Selecione a pasta do projeto
- Aguarde o Gradle Sync (~2-3 min na primeira vez)

**4. Execute o app**
- Conecte um dispositivo ou abra o emulador
- Clique em **Run** (▶️) ou pressione `Shift + F10`

### Verificar Instalação

Após executar, verifique no Logcat:
```
✅ Supabase conectado com sucesso!
✅ Room Database criado
✅ MapLibre inicializado
```

### Primeiro Uso

1. **Permissões**: Aceite localização e câmera quando solicitado
2. **Mapa**: Aguarde carregamento dos tiles (OpenStreetMap)
3. **Coleta**: Toque no mapa para adicionar uma amostra
4. **Sincronização**: Dados sincronizam automaticamente com internet

## 📦 Dependências Principais

### libs.versions.toml

```toml
[versions]
maplibre = "11.5.1"        # MapLibre Native (Open Source)
room = "2.6.1"             # Room Database
camerax = "1.4.1"          # CameraX
hilt = "2.52"              # Hilt DI
workManager = "2.10.0"     # WorkManager
compose = "2024.12.01"     # Compose BOM
```

### Repositório Maven do MapLibre

O MapLibre requer um repositório Maven adicional. Isso já está configurado em [settings.gradle.kts](settings.gradle.kts):

```kotlin
maven {
    url = uri("https://maven.maplibre.org/releases/")
}
```

## 🗺️ MapLibre: Configuração

### Por que MapLibre?

- ✅ **100% Open Source** (BSD-2-Clause)
- ✅ **Sem custos de API**
- ✅ **Baseado em OpenGL** (alta performance)
- ✅ **Funciona offline** com tiles cacheados
- ✅ **Fork do Mapbox GL** (mantendo compatibilidade)

### Estilo do Mapa

O app usa um estilo customizado que carrega tiles do OpenStreetMap:

```json
{
  "version": 8,
  "sources": {
    "osm": {
      "type": "raster",
      "tiles": ["https://tile.openstreetmap.org/{z}/{x}/{y}.png"],
      "tileSize": 256,
      "maxzoom": 19
    }
  }
}
```

### Overlay de Fazenda

O código demonstra como adicionar polígonos (GeoJSON) sobre o mapa:

```kotlin
val geoJsonSource = GeoJsonSource("farm-boundary-source", featureCollection)
style.addSource(geoJsonSource)

val fillLayer = FillLayer("farm-boundary-layer", "farm-boundary-source")
    .withProperties(
        fillColor("#88FF6B35"),
        fillOpacity(0.4f)
    )
```

## 💾 Banco de Dados Offline

### Entidade SoilSample

```kotlin
@Entity(tableName = "soil_samples")
data class SoilSample(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val note: String,
    val photoPath: String?,     // ⚠️ Path, não BLOB!
    val timestamp: Long,
    val isSynced: Boolean = false
)
```

### Por que não salvar BLOB?

- ❌ **Performance**: Queries lentas em imagens grandes
- ❌ **Memória**: Aumenta o tamanho do banco exponencialmente
- ✅ **Solução**: Salvar no `filesDir` e guardar apenas o path

## 📸 Otimização de Fotos

O [ImageFileUtils.kt](app/src/main/java/com/agrogeocolector/util/ImageFileUtils.kt) implementa:

1. **Compressão JPEG 80%**: Reduz 70-80% do tamanho
2. **Redimensionamento**: Max 1920px (mantém proporção)
3. **Correção EXIF**: Rotação automática
4. **Salvamento interno**: `filesDir/sample_photos/`

```kotlin
val savedPath = ImageFileUtils.saveAndCompressImage(context, photoUri)
// savedPath: /data/user/0/com.agrogeocolector/files/sample_photos/SAMPLE_20231211_143022.jpg
```

## 🔄 Sincronização em Background

### WorkManager

O [SyncWorker.kt](app/src/main/java/com/agrogeocolector/data/sync/SyncWorker.kt) sincroniza automaticamente:

- ⏰ **Periódica**: A cada 15 minutos (quando há internet)
- 📡 **Constraint**: `NetworkType.CONNECTED`
- 🔁 **Retry**: Backoff exponencial
- 🔋 **Battery-friendly**: Usa doze mode

```kotlin
// Agenda sync periódica
SyncManager.schedulePeriodicSync(context)

// Sync imediata
SyncManager.syncNow(context)
```

## 🎨 UI com Jetpack Compose

### MapLibre em Compose

O MapLibre usa Views clássicas. Para integrá-lo ao Compose:

```kotlin
AndroidView(
    factory = { ctx ->
        MapView(ctx).apply {
            getMapAsync { map ->
                map.setStyle(getOSMStyle()) { style ->
                    // Configurar mapa
                }
            }
        }
    }
)
```

### Gerenciamento de Ciclo de Vida

```kotlin
DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> mapView?.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
            // ...
        }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
        lifecycleOwner.lifecycle.removeObserver(observer)
        mapView?.onDestroy()
    }
}
```

## 🔧 Roadmap

- [x] Mapa offline com MapLibre + OSM
- [x] Localização GPS em tempo real
- [x] Captura e compressão de fotos
- [x] Banco de dados Room
- [x] Sincronização com Supabase
- [x] Background sync com WorkManager
- [ ] Tela de lista de amostras
- [ ] Formulário de coleta detalhado
- [ ] Exportação de dados (CSV/GeoJSON/KML)
- [ ] Desenho de talhões no mapa
- [ ] Importação de shapefiles
- [ ] Autenticação de usuários
- [ ] Modo offline completo (cache de tiles)
- [ ] Relatórios e estatísticas

📖 Veja mais detalhes em [CHECKLIST.md](CHECKLIST.md)

## 📱 Requisitos de Sistema

- **Android**: 8.0 (API 26) ou superior
- **RAM**: 2GB mínimo
- **Armazenamento**: 100MB para o app + espaço para fotos
- **GPS**: Necessário para localização
- **Câmera**: Recomendado para fotos de amostras
- **Internet**: Opcional (apenas para sincronização)

## 📚 Documentação

| Documento | Descrição |
|-----------|-----------|
| [QUICK_START.md](QUICK_START.md) | 🚀 Guia rápido (15 min) |
| [SUPABASE_SETUP.md](SUPABASE_SETUP.md) | ⚙️ Configurar backend |
| [DEVELOPMENT.md](DEVELOPMENT.md) | 🔧 Guia técnico detalhado |
| [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) | 🏗️ Arquitetura do projeto |
| [SYNC_IMPLEMENTATION.md](SYNC_IMPLEMENTATION.md) | 🔄 Como funciona a sincronização |
| [CHECKLIST.md](CHECKLIST.md) | ✅ Roadmap completo |

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👨‍💻 Autor

**Saulo Rodrigues**
- GitHub: [@SauloRodrigues20](https://github.com/SauloRodrigues20)

## 🙏 Agradecimentos

- [MapLibre](https://maplibre.org/) - Mapas open source
- [OpenStreetMap](https://www.openstreetmap.org/) - Dados cartográficos
- [Supabase](https://supabase.com/) - Backend as a Service
- Comunidade Android pela excelente documentação

---

**🌱 Desenvolvido com ❤️ para a Agronomia Brasileira 🇧🇷**

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Fork o projeto
2. Crie uma branch (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👨‍💻 Autor

**Saulo Rodrigues**
- GitHub: [@SauloRodrigues20](https://github.com/SauloRodrigues20)

## 🙏 Agradecimentos

- [MapLibre](https://maplibre.org/) - Mapas Open Source
- [OpenStreetMap](https://www.openstreetmap.org/) - Dados cartográficos
- [Android Jetpack](https://developer.android.com/jetpack) - Stack moderna

---

**⭐ Se este projeto foi útil, considere dar uma estrela!**