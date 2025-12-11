# 🚀 Guia Rápido - AgroColetor

## ⏱️ Tempo estimado: 15 minutos

Este guia te levará do zero a um app funcionando em poucos passos!

---

## 📋 Pré-requisitos

### 1. Ferramentas Necessárias

- ✅ **Android Studio** (versão Hedgehog ou superior)
  - Download: https://developer.android.com/studio
  
- ✅ **JDK 17** (já vem com o Android Studio)

- ✅ **Conta no Supabase** (gratuita)
  - Criar em: https://supabase.com

### 2. Verificar Instalação

```bash
# No terminal do Android Studio
java -version  # Deve mostrar versão 17+
```

---

## 🎯 Passo a Passo

### Passo 1: Clonar o Repositório

```bash
git clone https://github.com/SauloRodrigues20/Agrogeocolector.git
cd Agrogeocolector
```

### Passo 2: Abrir no Android Studio

1. Abra o Android Studio
2. **File** → **Open**
3. Selecione a pasta `Agrogeocolector`
4. Aguarde o Gradle Sync (pode demorar 2-3 minutos na primeira vez)

### Passo 3: Configurar o Supabase

#### 3.1. Criar Projeto no Supabase

1. Acesse https://supabase.com e faça login
2. Clique em **"New Project"**
3. Preencha:
   - **Name**: AgroColetor
   - **Database Password**: Crie uma senha forte (salve!)
   - **Region**: South America (São Paulo)
4. Clique em **"Create new project"**
5. Aguarde ~2 minutos

#### 3.2. Criar Tabela no Banco

1. No dashboard, vá em **SQL Editor**
2. Clique em **"New query"**
3. Copie TODO o SQL de [SUPABASE_SETUP.md - Seção 2](SUPABASE_SETUP.md#2-configurar-banco-de-dados)
4. Clique em **"Run"** (ou `Ctrl + Enter`)
5. Verifique: "Success. No rows returned"

#### 3.3. Criar Bucket de Storage

1. Vá em **Storage** no menu lateral
2. Clique em **"Create a new bucket"**
3. Preencha:
   - **Name**: `soil-photos`
   - **Public bucket**: ✅ Marque
   - **File size limit**: 10 MB
4. Clique em **"Create bucket"**

#### 3.4. Obter Credenciais

1. Vá em **Settings** → **API**
2. Copie:
   - **Project URL**: `https://xxxxxxx.supabase.co`
   - **anon public**: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`

⚠️ **Use apenas a chave "anon public", NÃO a "service_role"!**

### Passo 4: Configurar Credenciais no App

#### Opção A: Arquivo local.properties (Recomendado)

1. Na raiz do projeto, copie o arquivo de exemplo:
   ```bash
   cp local.properties.example local.properties
   ```

2. Edite `local.properties` e preencha:
   ```properties
   SUPABASE_URL=https://seu-projeto.supabase.co
   SUPABASE_ANON_KEY=eyJhbGci...
   ```

3. Salve o arquivo

#### Opção B: Hardcoded (Apenas para testes)

1. Abra `app/src/main/java/com/agrogeocolector/di/SupabaseModule.kt`
2. Substitua as constantes:
   ```kotlin
   private const val SUPABASE_URL = "https://seu-projeto.supabase.co"
   private const val SUPABASE_ANON_KEY = "eyJhbGci..."
   ```

⚠️ **NUNCA faça commit de credenciais reais!**

### Passo 5: Executar o App

1. Conecte um dispositivo Android (API 26+) ou abra o emulador
2. No Android Studio, clique em **"Run"** (▶️) ou `Shift + F10`
3. Aguarde a compilação (~2 minutos na primeira vez)
4. O app será instalado automaticamente!

---

## 🧪 Testar Funcionalidades

### Teste 1: Verificar Logs

1. Abra o **Logcat** no Android Studio
2. Filtre por: `Supabase`
3. Você deve ver: `✅ Supabase conectado com sucesso!`

### Teste 2: Adicionar Amostra

1. No app, toque no mapa para abrir a tela de coleta
2. Tire uma foto
3. Adicione uma observação
4. Salve
5. A amostra deve aparecer na lista

### Teste 3: Verificar Sincronização

1. Conecte o dispositivo à internet
2. No Logcat, filtre por: `SyncWorker`
3. Você deve ver: `✅ Sincronização concluída com sucesso`
4. Abra o Supabase → **Table Editor** → `soil_samples`
5. Recarregue a tabela
6. Sua amostra deve estar lá!

### Teste 4: Verificar Foto

1. No Supabase, vá em **Storage** → `soil-photos`
2. Você deve ver a foto enviada
3. Clique na foto para visualizar

---

## ❓ Problemas Comuns

### "Gradle sync failed"

**Solução**: 
```bash
# No terminal do Android Studio
./gradlew clean
```
Depois: **File** → **Sync Project with Gradle Files**

### "Supabase connection failed"

**Solução**: Verifique:
1. Credenciais estão corretas em `local.properties`
2. Projeto Supabase está ativo (não pausado)
3. Dispositivo tem conexão com internet

### "Table soil_samples does not exist"

**Solução**: Execute o SQL de criação da tabela novamente no Supabase SQL Editor.

### "Permission denied for bucket"

**Solução**: Certifique-se que o bucket `soil-photos` é público:
1. Storage → soil-photos → Settings
2. Marque **"Public bucket"**
3. Salve

### App não compila

**Solução**:
```bash
# Limpar cache do Gradle
./gradlew clean
./gradlew --stop

# Reabrir o Android Studio
```

---

## 📚 Próximos Passos

Agora que o app está rodando:

1. 📖 Leia [DEVELOPMENT.md](DEVELOPMENT.md) - Detalhes técnicos
2. 🏗️ Veja [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) - Arquitetura
3. 🔄 Estude [SYNC_IMPLEMENTATION.md](SYNC_IMPLEMENTATION.md) - Como funciona a sincronização
4. ✅ Confira [CHECKLIST.md](CHECKLIST.md) - Roadmap de funcionalidades

---

## 🆘 Precisa de Ajuda?

- 📧 Abra uma **Issue** no GitHub
- 💬 Verifique as **Discussions**
- 📖 Leia a documentação completa

---

## 🎉 Sucesso!

Seu AgroColetor está rodando! Agora você tem:
- ✅ App Android funcionando
- ✅ Banco de dados configurado
- ✅ Sincronização automática
- ✅ Upload de fotos

**Bom desenvolvimento! 🌱**
