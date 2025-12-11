# 📥 Como Baixar e Usar o AgroColetor

## ⚡ Resumo Rápido

```bash
# 1. Baixar o projeto
git clone https://github.com/SauloRodrigues20/Agrogeocolector.git
cd Agrogeocolector

# 2. Configurar credenciais
cp local.properties.example local.properties
# Edite o arquivo local.properties com suas credenciais

# 3. Abrir no Android Studio e executar (Shift + F10)
```

**⏱️ Tempo total: 15 minutos**

---

## 📋 Passo a Passo Completo

### Passo 1: Instalar Ferramentas

#### 1.1. Instalar Android Studio

1. Acesse: https://developer.android.com/studio
2. Baixe a versão para seu sistema operacional
3. Instale seguindo as instruções
4. Abra o Android Studio pela primeira vez (vai baixar componentes)

#### 1.2. Verificar Java

O Android Studio já vem com Java 17. Verifique no terminal:

```bash
# No terminal do Android Studio (View → Tool Windows → Terminal)
java -version
```

Deve mostrar: `openjdk version "17"`

---

### Passo 2: Baixar o Projeto

#### 2.1. Clonar o Repositório

Abra o terminal e execute:

```bash
# Baixar o projeto
git clone https://github.com/SauloRodrigues20/Agrogeocolector.git

# Entrar na pasta
cd Agrogeocolector
```

#### 2.2. Abrir no Android Studio

1. Abra o Android Studio
2. Clique em **"Open"** (ou File → Open)
3. Navegue até a pasta `Agrogeocolector`
4. Clique em **"OK"**
5. Aguarde o **Gradle Sync** (~2-3 minutos)

---

### Passo 3: Criar Conta no Supabase

#### 3.1. Registrar-se

1. Acesse: https://supabase.com
2. Clique em **"Start your project"**
3. Faça login com GitHub ou email

#### 3.2. Criar Projeto

1. Clique em **"New Project"**
2. Preencha:
   - **Name**: `AgroColetor`
   - **Database Password**: Crie uma senha forte (anote!)
   - **Region**: `South America (São Paulo)` ou mais próximo
3. Clique em **"Create new project"**
4. Aguarde ~2 minutos

#### 3.3. Criar Banco de Dados

1. No dashboard, vá em **SQL Editor** (menu lateral)
2. Clique em **"New query"**
3. Copie o SQL do arquivo [SUPABASE_SETUP.md - Seção 2](SUPABASE_SETUP.md#2-configurar-banco-de-dados)
4. Cole no editor
5. Clique em **"Run"** (ou `Ctrl + Enter`)
6. Aguarde: "Success. No rows returned"

#### 3.4. Criar Storage

1. Vá em **Storage** (menu lateral)
2. Clique em **"Create a new bucket"**
3. Preencha:
   - **Name**: `soil-photos`
   - **Public bucket**: ✅ Marque
   - **File size limit**: `10 MB`
4. Clique em **"Create bucket"**

#### 3.5. Copiar Credenciais

1. Vá em **Settings** → **API** (menu lateral)
2. Copie:
   - **Project URL**: `https://xxxxxxx.supabase.co`
   - **anon public**: `eyJhbGci...` (chave grande)

⚠️ **Use APENAS a chave "anon public"**, não use "service_role"!

---

### Passo 4: Configurar Credenciais

#### 4.1. Criar arquivo de configuração

No terminal, dentro da pasta do projeto:

```bash
# Copiar o template
cp local.properties.example local.properties
```

#### 4.2. Editar credenciais

Abra o arquivo `local.properties` em qualquer editor de texto e adicione suas credenciais:

```properties
# Cole aqui a URL que você copiou
SUPABASE_URL=https://seu-projeto-aqui.supabase.co

# Cole aqui a chave anon que você copiou
SUPABASE_ANON_KEY=eyJhbGciOi...sua-chave-completa-aqui
```

**Salve o arquivo!**

---

### Passo 5: Executar o App

#### 5.1. Conectar Dispositivo

**Opção A: Dispositivo Físico**

1. Conecte seu celular Android no computador (USB)
2. No celular:
   - Vá em **Configurações** → **Sobre o telefone**
   - Toque 7x em **"Número da versão"**
   - Volte e entre em **Opções do desenvolvedor**
   - Ative **"Depuração USB"**
3. Autorize no celular quando aparecer o popup

**Opção B: Emulador**

1. No Android Studio, clique em **"Device Manager"** (ícone de celular)
2. Clique em **"Create Virtual Device"**
3. Selecione: **Pixel 6** ou similar
4. System Image: **Android 13** (API 33)
5. Clique em **"Finish"**
6. Clique em **▶️ Play** para iniciar o emulador

#### 5.2. Executar

1. No Android Studio, selecione o dispositivo na barra superior
2. Clique no botão **▶️ Run** (ou pressione `Shift + F10`)
3. Aguarde a compilação (~2 minutos na primeira vez)
4. O app será instalado automaticamente!

---

### Passo 6: Testar o App

#### 6.1. Ao Abrir

1. Aceite as permissões de **Localização** e **Câmera**
2. Aguarde o mapa carregar (tiles do OpenStreetMap)
3. Veja sua localização aparecer no mapa (ponto azul)

#### 6.2. Adicionar Amostra

1. Toque no mapa ou no botão **+** (FAB)
2. Tire uma foto
3. Adicione uma observação
4. Salve

#### 6.3. Verificar Sincronização

No **Logcat** do Android Studio:

1. Clique em **Logcat** (parte inferior)
2. Filtre por: `Supabase`
3. Você deve ver:
   ```
   ✅ Supabase conectado com sucesso!
   ```

4. Filtre por: `SyncWorker`
5. Você deve ver:
   ```
   SyncWorker: Iniciando sincronização...
   SyncWorker: ✅ Sincronização concluída com sucesso
   ```

#### 6.4. Ver Dados no Supabase

1. Abra o dashboard do Supabase
2. Vá em **Table Editor** → `soil_samples`
3. Clique em **"Refresh"** (🔄)
4. Sua amostra deve aparecer!

5. Vá em **Storage** → `soil-photos`
6. A foto deve estar lá!

---

## ❓ Problemas Comuns

### "Gradle sync failed"

**Solução:**
```bash
# No terminal do Android Studio
./gradlew clean
```
Depois: **File** → **Invalidate Caches** → **Invalidate and Restart**

---

### "Supabase connection failed"

**Verifique:**

1. ✅ Credenciais corretas em `local.properties`
2. ✅ Projeto Supabase está ativo (não pausado)
3. ✅ Dispositivo tem internet
4. ✅ Usou a chave "anon public" (não "service_role")

**Teste manual:**

Abra o navegador e acesse:
```
https://seu-projeto.supabase.co/rest/v1/
```

Deve aparecer uma mensagem do Supabase (não erro 404).

---

### "Permission denied for bucket"

**Solução:**

1. No Supabase, vá em **Storage** → `soil-photos`
2. Clique em **Settings** (⚙️)
3. Marque **"Public bucket"**
4. Clique em **"Save"**

---

### "Table soil_samples does not exist"

**Solução:**

Execute o SQL de criação novamente:

1. Supabase → **SQL Editor**
2. Copie o SQL de [SUPABASE_SETUP.md](SUPABASE_SETUP.md#2-configurar-banco-de-dados)
3. Execute

---

### App não compila / Erros no Gradle

**Solução:**

```bash
# Limpar completamente
./gradlew clean
./gradlew --stop

# Deletar caches
rm -rf .gradle
rm -rf build
rm -rf app/build

# Reabrir Android Studio
```

---

## 📱 Requisitos Mínimos

### Computador (para desenvolvimento)

- **OS**: Windows 10+, macOS 10.14+, ou Linux (Ubuntu 18+)
- **RAM**: 8GB mínimo, 16GB recomendado
- **Espaço**: 10GB livres (Android Studio + SDKs)
- **Internet**: Para download de dependências

### Dispositivo Android (para testar)

- **Android**: 8.0 (API 26) ou superior
- **RAM**: 2GB
- **Armazenamento**: 100MB
- **GPS**: Sim
- **Câmera**: Recomendado

---

## 🎯 Comandos Resumidos

### Para quem já tem tudo instalado:

```bash
# 1. Baixar
git clone https://github.com/SauloRodrigues20/Agrogeocolector.git
cd Agrogeocolector

# 2. Configurar
cp local.properties.example local.properties
nano local.properties  # ou qualquer editor
# Adicione SUPABASE_URL e SUPABASE_ANON_KEY

# 3. Abrir Android Studio
# File → Open → Selecionar pasta

# 4. Executar
# Shift + F10
```

---

## 📚 Documentação Adicional

| Documento | Quando Usar |
|-----------|-------------|
| [QUICK_START.md](QUICK_START.md) | Guia visual passo a passo |
| [SUPABASE_SETUP.md](SUPABASE_SETUP.md) | Detalhes do backend |
| [DEVELOPMENT.md](DEVELOPMENT.md) | Referência técnica |
| [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) | Entender o código |

---

## 🆘 Precisa de Ajuda?

1. 📖 Leia primeiro: [QUICK_START.md](QUICK_START.md)
2. 🔍 Veja: [Issues no GitHub](https://github.com/SauloRodrigues20/Agrogeocolector/issues)
3. ❓ Abra uma nova Issue se não encontrar solução
4. 💬 Use as [Discussions](https://github.com/SauloRodrigues20/Agrogeocolector/discussions)

---

## ✅ Checklist de Sucesso

Marque conforme completa:

- [ ] Android Studio instalado
- [ ] Projeto clonado
- [ ] Conta Supabase criada
- [ ] Banco de dados criado
- [ ] Storage bucket criado
- [ ] Credenciais configuradas em `local.properties`
- [ ] App compila sem erros
- [ ] App executa no dispositivo
- [ ] Permissões aceitas
- [ ] Mapa carregou
- [ ] Amostra adicionada
- [ ] Sincronização funcionando
- [ ] Dados aparecem no Supabase

**Se marcou tudo: 🎉 Parabéns! Seu AgroColetor está funcionando!**

---

**🌱 Desenvolvido para a Agronomia Brasileira 🇧🇷**
