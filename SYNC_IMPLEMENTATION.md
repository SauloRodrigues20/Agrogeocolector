# 🎯 Sincronização com Supabase - Implementação Completa

## ✅ O Que Foi Implementado

### 1. Dependências Adicionadas

**gradle/libs.versions.toml**:
```toml
supabase = "2.6.0"
ktor = "2.3.12"

# Bibliotecas
supabase-postgrest-kt (PostgreSQL REST API)
supabase-storage-kt (Upload de arquivos)
supabase-gotrue-kt (Autenticação)
ktor-client-android (Cliente HTTP)
```

### 2. Arquivos Criados

#### `SoilSampleRemote.kt`
Modelo de dados para envio ao Supabase com serialização JSON.

#### `SupabaseRepository.kt`
Repositório que encapsula:
- `uploadImage(file)`: Upload de fotos para Storage
- `insertSample(sample)`: Inserção no PostgreSQL
- `deleteImage(url)`: Limpeza de fotos
- `testConnection()`: Teste de conectividade

#### `SupabaseModule.kt`
Módulo Hilt que provê o `SupabaseClient` configurado.

#### `SoilSampleRepository.kt`
Repositório unificado que:
- Abstrai o DAO do Room
- Gerencia sincronização automática
- API limpa para ViewModels

#### `SUPABASE_SETUP.md`
Guia completo de configuração do Supabase.

### 3. Arquivos Atualizados

#### `SyncWorker.kt`
Completamente reescrito para:
1. Buscar amostras não sincronizadas
2. Upload de fotos (se existirem)
3. Inserir dados no Supabase
4. Marcar como sincronizado no Room
5. Tratamento robusto de erros

#### `SyncManager.kt`
Melhorado com:
- `syncAfterSave()`: Sincroniza após salvar
- `getSyncStats()`: Estatísticas de sincronização
- Logs detalhados
- Configurações otimizadas

#### `SamplesViewModel.kt`
Atualizado para agendar sincronização após salvar:
```kotlin
soilSampleDao.insertSample(sample)
SyncManager.syncAfterSave(context) // ← NOVO!
```

---

## 🔄 Fluxo Completo de Sincronização

### 1. Usuário Salva Amostra

```kotlin
// ViewModel ou Repository
viewModelScope.launch {
    val sample = SoilSample(
        latitude = -15.7940,
        longitude = -47.8825,
        note = "Solo argiloso",
        photoPath = "/data/.../photo.jpg",
        isSynced = false // ← Importante!
    )
    
    soilSampleDao.insertSample(sample)
    
    // Agenda sincronização
    SyncManager.syncAfterSave(context)
}
```

### 2. WorkManager Executa SyncWorker

Quando há internet, o WorkManager executa:

```kotlin
override suspend fun doWork(): Result {
    // 1. Busca amostras não sincronizadas
    val unsyncedSamples = soilSampleDao.getUnsyncedSamples()
    
    for (sample in unsyncedSamples) {
        // 2. Upload da foto (se houver)
        var photoUrl: String? = null
        if (sample.photoPath != null) {
            val file = File(sample.photoPath)
            photoUrl = supabaseRepository.uploadImage(file)
        }
        
        // 3. Monta objeto remoto
        val remoteSample = SoilSampleRemote(
            latitude = sample.latitude,
            longitude = sample.longitude,
            note = sample.note,
            photoUrl = photoUrl, // ← URL da foto no Supabase
            timestamp = sample.timestamp
        )
        
        // 4. Insere no PostgreSQL
        val remoteId = supabaseRepository.insertSample(remoteSample)
        
        // 5. Marca como sincronizado
        soilSampleDao.markAsSynced(sample.id, remoteId)
    }
    
    return Result.success()
}
```

### 3. Dados no Supabase

Após sincronização:

**Storage (`soil-photos`):**
```
/20231211_143022_photo.jpg
/20231211_145533_photo.jpg
...
```

**PostgreSQL (`soil_samples`):**
```sql
id                  | latitude   | longitude   | photo_url                                | timestamp      | isSynced
--------------------|------------|-------------|------------------------------------------|----------------|----------
uuid-1234-abcd...   | -15.7940   | -47.8825    | https://...supabase.co/.../photo.jpg     | 1702314000000  | true
```

---

## 📊 Configuração do Supabase

### Passo 1: Criar Conta
1. Acesse https://supabase.com
2. Crie uma conta
3. Crie um novo projeto

### Passo 2: Configurar Banco

Execute no SQL Editor:

```sql
CREATE TABLE soil_samples (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES auth.users(id),
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    altitude DOUBLE PRECISION,
    accuracy REAL,
    note TEXT,
    photo_url TEXT,
    timestamp BIGINT NOT NULL,
    farm_id TEXT,
    field_id TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Habilitar RLS
ALTER TABLE soil_samples ENABLE ROW LEVEL SECURITY;

-- Política de inserção
CREATE POLICY "Users can insert their own samples"
ON soil_samples FOR INSERT
WITH CHECK (auth.uid() = user_id);
```

### Passo 3: Configurar Storage

1. Crie bucket `soil-photos`
2. Marque como público
3. Configure políticas de upload

### Passo 4: Obter Credenciais

Em Settings → API:
- **Project URL**: `https://xxxxxxxxxxx.supabase.co`
- **anon key**: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`

### Passo 5: Configurar no App

Crie `local.properties`:

```properties
SUPABASE_URL=https://xxxxxxxxxxx.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Atualize `SupabaseModule.kt`:

```kotlin
private const val SUPABASE_URL = BuildConfig.SUPABASE_URL
private const val SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY
```

---

## 🧪 Como Testar

### 1. Teste de Conectividade

```kotlin
viewModelScope.launch {
    val connected = supabaseRepository.testConnection()
    Log.d("Test", "Conectado: $connected")
}
```

### 2. Teste de Upload de Foto

```kotlin
val file = File("/path/to/photo.jpg")
val url = supabaseRepository.uploadImage(file)
Log.d("Test", "Foto enviada: $url")
```

### 3. Teste de Inserção

```kotlin
val sample = SoilSampleRemote(
    latitude = -15.7940,
    longitude = -47.8825,
    note = "Teste",
    timestamp = System.currentTimeMillis()
)
val id = supabaseRepository.insertSample(sample)
Log.d("Test", "ID remoto: $id")
```

### 4. Teste de Sincronização Completa

1. Adicione uma amostra no app (offline)
2. Verifique no Room: `isSynced = false`
3. Conecte à internet
4. Aguarde ou force: `SyncManager.syncNow(context)`
5. Verifique logs: `SyncWorker: ✅ Amostra sincronizada`
6. Verifique no Supabase (Table Editor)
7. Verifique no Room: `isSynced = true`

---

## 🔍 Debugging

### Logs do SyncWorker

```
D/SyncWorker: Iniciando sincronização...
D/SyncWorker: Encontradas 3 amostras para sincronizar
D/SyncWorker: Sincronizando amostra ID: 1
D/SyncWorker: Fazendo upload da foto: photo.jpg
D/SupabaseRepository: Upload concluído: https://...
D/SyncWorker: Inserindo dados no Supabase...
D/SupabaseRepository: Amostra inserida com sucesso. ID: uuid-1234
D/SyncWorker: ✅ Amostra 1 sincronizada com sucesso!
D/SyncWorker: Sincronização concluída: 3 sucesso(s), 0 falha(s)
```

### Verificar WorkManager

```kotlin
val workInfos = WorkManager.getInstance(context)
    .getWorkInfosByTag("sync")
    .get()

workInfos.forEach { info ->
    Log.d("Work", "Estado: ${info.state}")
    Log.d("Work", "Tentativas: ${info.runAttemptCount}")
}
```

### Verificar Supabase

1. **Logs**: Dashboard → Logs → API
2. **Storage**: Dashboard → Storage → soil-photos
3. **Database**: Dashboard → Table Editor → soil_samples

---

## ⚠️ Problemas Comuns

### "Invalid API key"
❌ Você usou a chave `service_role` em vez da `anon public`.

### "row violates row-level security"
❌ Tabela tem RLS habilitado mas usuário não autenticado.  
✅ Soluções:
- Desabilite RLS para testes: `ALTER TABLE soil_samples DISABLE ROW LEVEL SECURITY;`
- Ou implemente autenticação com GoTrue

### "Permission denied for bucket"
❌ Bucket não é público ou sem políticas corretas.  
✅ Marque bucket como público ou adicione políticas.

### Sincronização não executa
❌ WorkManager constraints não atendidos (sem internet).  
✅ Verifique internet e force: `SyncManager.syncNow(context)`

---

## 📈 Estatísticas

Execute para ver estatísticas:

```kotlin
val stats = SyncManager.getSyncStats(context)
Log.d("Stats", """
    Running: ${stats.running}
    Enqueued: ${stats.enqueued}
    Succeeded: ${stats.succeeded}
    Failed: ${stats.failed}
    Has pending: ${stats.hasPending}
""".trimIndent())
```

---

## 🚀 Próximos Passos

### Funcionalidades Futuras

1. **Autenticação**
   - Login/registro com email
   - OAuth (Google, GitHub)
   - Perfil do usuário

2. **Sincronização Bidirecional**
   - Download de amostras de outros dispositivos
   - Resolução de conflitos
   - Merge de dados

3. **Otimizações**
   - Batch upload (várias amostras de uma vez)
   - Upload incremental de fotos
   - Compressão de dados

4. **Realtime**
   - Notificações de novas amostras
   - Colaboração em tempo real
   - Chat entre agrônomos

---

## 📚 Recursos

- **Supabase Docs**: https://supabase.com/docs
- **Kotlin Client**: https://github.com/supabase-community/supabase-kt
- **WorkManager**: https://developer.android.com/topic/libraries/architecture/workmanager

---

**✅ Sincronização implementada e pronta para uso!**

**Custos**: $0 (plano gratuito do Supabase)  
**Limite**: 500 MB storage + 2 GB transferência/mês  
**Upgrade**: A partir de $25/mês para produção
