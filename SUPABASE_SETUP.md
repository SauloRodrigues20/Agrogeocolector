# 🔄 Guia de Configuração do Supabase

## 📋 Índice

1. [Criar Conta no Supabase](#criar-conta-no-supabase)
2. [Configurar Banco de Dados](#configurar-banco-de-dados)
3. [Configurar Storage](#configurar-storage)
4. [Obter Credenciais](#obter-credenciais)
5. [Configurar no App](#configurar-no-app)
6. [Testar Sincronização](#testar-sincronização)

---

## 1. Criar Conta no Supabase

### Passo 1: Acesse o Supabase
1. Vá para https://supabase.com
2. Clique em "Start your project"
3. Crie uma conta (pode usar GitHub)

### Passo 2: Criar Novo Projeto
1. Clique em "New Project"
2. Preencha:
   - **Name**: AgroColetor
   - **Database Password**: Crie uma senha forte (salve em lugar seguro!)
   - **Region**: South America (São Paulo) ou mais próximo
   - **Pricing Plan**: Free (para começar)
3. Clique em "Create new project"
4. Aguarde ~2 minutos para o projeto ser criado

---

## 2. Configurar Banco de Dados

### Criar Tabela `soil_samples`

1. No dashboard do Supabase, vá em **SQL Editor**
2. Clique em "New query"
3. Cole o SQL abaixo:

```sql
-- Criar extensão para UUID (se não existir)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Criar tabela de amostras de solo
CREATE TABLE soil_samples (
    -- ID único gerado automaticamente
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- ID do usuário (relaciona com auth.users)
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    
    -- Coordenadas GPS
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    altitude DOUBLE PRECISION,
    accuracy REAL,
    
    -- Observações
    note TEXT,
    
    -- URL da foto no Storage
    photo_url TEXT,
    
    -- Timestamp da coleta (em milissegundos Unix)
    timestamp BIGINT NOT NULL,
    
    -- Identificadores de fazenda/talhão
    farm_id TEXT,
    field_id TEXT,
    
    -- Metadados
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Criar índices para otimizar queries
CREATE INDEX idx_soil_samples_user_id ON soil_samples(user_id);
CREATE INDEX idx_soil_samples_timestamp ON soil_samples(timestamp);
CREATE INDEX idx_soil_samples_farm_id ON soil_samples(farm_id);
CREATE INDEX idx_soil_samples_created_at ON soil_samples(created_at);

-- Criar trigger para atualizar updated_at automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_soil_samples_updated_at
    BEFORE UPDATE ON soil_samples
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comentários para documentação
COMMENT ON TABLE soil_samples IS 'Amostras de solo coletadas pelos usuários';
COMMENT ON COLUMN soil_samples.latitude IS 'Latitude em graus decimais';
COMMENT ON COLUMN soil_samples.longitude IS 'Longitude em graus decimais';
COMMENT ON COLUMN soil_samples.timestamp IS 'Timestamp Unix em milissegundos';
```

4. Clique em "Run" ou pressione `Ctrl + Enter`
5. Verifique se apareceu "Success. No rows returned"

### Configurar Row Level Security (RLS)

⚠️ **IMPORTANTE**: Sem RLS, qualquer pessoa pode acessar seus dados!

1. No SQL Editor, execute:

```sql
-- Habilitar RLS na tabela
ALTER TABLE soil_samples ENABLE ROW LEVEL SECURITY;

-- Política: Usuários podem inserir suas próprias amostras
CREATE POLICY "Users can insert their own samples"
ON soil_samples FOR INSERT
WITH CHECK (auth.uid() = user_id);

-- Política: Usuários podem ver suas próprias amostras
CREATE POLICY "Users can view their own samples"
ON soil_samples FOR SELECT
USING (auth.uid() = user_id);

-- Política: Usuários podem atualizar suas próprias amostras
CREATE POLICY "Users can update their own samples"
ON soil_samples FOR UPDATE
USING (auth.uid() = user_id)
WITH CHECK (auth.uid() = user_id);

-- Política: Usuários podem deletar suas próprias amostras
CREATE POLICY "Users can delete their own samples"
ON soil_samples FOR DELETE
USING (auth.uid() = user_id);
```

### Verificar Tabela Criada

1. Vá em **Table Editor** no menu lateral
2. Você deve ver a tabela `soil_samples`
3. Clique nela para ver a estrutura

---

## 3. Configurar Storage

### Criar Bucket para Fotos

1. No menu lateral, vá em **Storage**
2. Clique em "Create a new bucket"
3. Preencha:
   - **Name**: `soil-photos`
   - **Public bucket**: ✅ Marque (para fotos serem acessíveis)
   - **File size limit**: 10 MB
   - **Allowed MIME types**: `image/jpeg`, `image/jpg`
4. Clique em "Create bucket"

### Configurar Políticas do Storage

1. Com o bucket selecionado, clique em "Policies"
2. Clique em "New Policy"
3. Cole o SQL:

```sql
-- Política: Qualquer pessoa autenticada pode fazer upload
CREATE POLICY "Authenticated users can upload photos"
ON storage.objects FOR INSERT
TO authenticated
WITH CHECK (bucket_id = 'soil-photos');

-- Política: Fotos são públicas (qualquer um pode ver)
CREATE POLICY "Photos are publicly accessible"
ON storage.objects FOR SELECT
TO public
USING (bucket_id = 'soil-photos');

-- Política: Usuários podem deletar suas próprias fotos
CREATE POLICY "Users can delete their own photos"
ON storage.objects FOR DELETE
TO authenticated
USING (bucket_id = 'soil-photos' AND auth.uid() = owner);
```

---

## 4. Obter Credenciais

### Passo 1: Acessar Configurações

1. No menu lateral, clique em **Settings** (⚙️)
2. Vá em **API**

### Passo 2: Copiar Credenciais

Você verá:

```
Project URL:
https://zwxobbirovyeudkzmxze.supabase.co

Project API keys:
anon public: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inp3eG9iYmlyb3Z5ZXVka3pteHplIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjU0NTIzMzcsImV4cCI6MjA4MTAyODMzN30.BR9R2TtSaylsQNAwKzWFDhZzvC8t2htVY2pLVTmf6JI
service_role: (não mostrado - use apenas em backend)
```

⚠️ **Use a chave `anon public`, NÃO a `service_role`!**

---

## 5. Configurar no App

### Opção 1: Arquivo local.properties (Recomendado)

1. Crie o arquivo `local.properties` na raiz do projeto:

```properties
# Supabase Configuration
SUPABASE_URL=https://zwxobbirovyeudkzmxze.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inp3eG9iYmlyb3Z5ZXVka3pteHplIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjU0NTIzMzcsImV4cCI6MjA4MTAyODMzN30.BR9R2TtSaylsQNAwKzWFDhZzvC8t2htVY2pLVTmf6JI
```

2. Adicione ao `.gitignore`:

```gitignore
local.properties
```

3. Atualize `app/build.gradle.kts`:

```kotlin
import java.util.Properties
import java.io.FileInputStream

android {
    buildFeatures {
        buildConfig = true
    }
    
    defaultConfig {
        // Lê credenciais do local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }
        
        buildConfigField(
            "String",
            "SUPABASE_URL",
            "\"${localProperties.getProperty("SUPABASE_URL", "")}\""
        )
        buildConfigField(
            "String",
            "SUPABASE_ANON_KEY",
            "\"${localProperties.getProperty("SUPABASE_ANON_KEY", "")}\""
        )
    }
}
```

4. Use no código:

```kotlin
// SupabaseModule.kt
private val SUPABASE_URL = BuildConfig.SUPABASE_URL
private val SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY
```

### Opção 2: Hardcoded (Apenas para testes locais!)

⚠️ **NUNCA FAÇA COMMIT DE CREDENCIAIS REAIS!**

```kotlin
// SupabaseModule.kt
private const val SUPABASE_URL = "https://zwxobbirovyeudkzmxze.supabase.co"
private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inp3eG9iYmlyb3Z5ZXVka3pteHplIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjU0NTIzMzcsImV4cCI6MjA4MTAyODMzN30.BR9R2TtSaylsQNAwKzWFDhZzvC8t2htVY2pLVTmf6JI"
```

---

## 6. Testar Sincronização

### Teste 1: Conexão Básica

1. Abra o app no Android Studio
2. No Logcat, filtre por "Supabase"
3. Você deve ver: "✅ Supabase conectado"

### Teste 2: Inserir Dados Manualmente

1. No SQL Editor do Supabase:

```sql
INSERT INTO soil_samples (
    latitude, longitude, note, timestamp
) VALUES (
    -15.7940, -47.8825, 'Teste manual', 1702314000000
);
```

2. Vá em **Table Editor** → `soil_samples`
3. Você deve ver o registro inserido

### Teste 3: Sincronização do App

1. No app, adicione uma amostra
2. Verifique o Logcat:
   - `SyncWorker: Iniciando sincronização...`
   - `SyncWorker: Fazendo upload da foto...`
   - `SyncWorker: ✅ Amostra sincronizada`
3. Vá no Supabase → **Table Editor**
4. Recarregue a tabela
5. Você deve ver a nova amostra!

### Teste 4: Verificar Foto

1. Vá em **Storage** → `soil-photos`
2. Você deve ver as fotos enviadas
3. Clique em uma foto
4. Copie a URL e abra no navegador
5. A foto deve carregar

---

## 🔧 Troubleshooting

### Erro: "Invalid API key"

**Solução**: Verifique se copiou a chave `anon public`, não a `service_role`.

### Erro: "new row violates row-level security"

**Solução**: Você não está autenticado. Para testes, pode desabilitar RLS:

```sql
ALTER TABLE soil_samples DISABLE ROW LEVEL SECURITY;
```

⚠️ **Mas reative em produção!**

### Erro: "relation does not exist"

**Solução**: A tabela não foi criada. Execute o SQL de criação novamente.

### Erro: "Permission denied for bucket"

**Solução**: Verifique as políticas do Storage. Certifique-se que o bucket é público ou tem políticas corretas.

### Fotos não aparecem

**Solução**:
1. Verifique se o bucket `soil-photos` existe
2. Verifique se é público
3. Verifique se o arquivo foi enviado (vá em Storage)
4. Teste a URL diretamente no navegador

---

## 📊 Monitoramento

### Dashboard do Supabase

1. **Logs**: Veja requisições em tempo real
2. **API Logs**: Monitore erros de API
3. **Database**: Veja queries executadas
4. **Storage**: Monitore uploads

### Alertas

Configure alertas para:
- Limite de storage atingido
- Muitos erros de API
- Uso de banco de dados alto

---

## 🚀 Próximos Passos

### Produção

1. **Autenticação**: Implemente login/registro
2. **RLS**: Certifique-se que está habilitado
3. **Backups**: Configure backups automáticos
4. **Rate Limiting**: Configure limites de requisições
5. **CDN**: Configure CDN para fotos

### Otimizações

1. **Índices**: Adicione índices para queries frequentes
2. **Cache**: Implemente cache local de dados
3. **Batch Upload**: Envie múltiplas amostras de uma vez
4. **Compressão**: Comprima fotos ainda mais

---

## 📚 Recursos Úteis

- **Documentação**: https://supabase.com/docs
- **API Reference**: https://supabase.com/docs/reference/javascript
- **Kotlin Client**: https://github.com/supabase-community/supabase-kt
- **Exemplos**: https://github.com/supabase/supabase/tree/master/examples

---

**✅ Configuração completa! Seu app agora sincroniza com o Supabase!**
