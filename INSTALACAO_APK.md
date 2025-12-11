# 📱 Como Instalar o AgroColetor

## ⚡ Instalação Super Simples

**Igual instalar WhatsApp, Instagram ou qualquer app!**

> ⏱️ **Tempo total: 5 minutos**  
> 📱 **Funciona em qualquer Android** (versão 8.0 ou mais nova)  
> 💰 **100% Gratuito** - sem custos escondidos

---

## 🎯 3 Passos Simples

### 📥 PASSO 1: Baixar o App

**No seu celular Android:**

1. Abra o navegador (Chrome, Firefox, etc)
2. Entre neste link: https://github.com/SauloRodrigues20/Agrogeocolector/releases
3. ⚠️ **Se aparecer "Não há nenhum lançamento aqui":**
   - Significa que ainda não foi criada a primeira versão
   - Aguarde a primeira release ser publicada
   - Ou veja [CRIAR_PRIMEIRA_RELEASE.md](CRIAR_PRIMEIRA_RELEASE.md) se você for o desenvolvedor
4. Toque na versão mais recente (por exemplo: **v1.0.0**)
5. Role a tela até ver **"Assets"** (arquivos)
6. Toque em **app-debug.apk** para baixar
7. Aguarde o download terminar (aparece uma notificação)

**💡 Dica:** O arquivo baixado fica na pasta **Downloads** do seu celular.

---

### 🔓 PASSO 2: Permitir Instalação

Quando você tentar instalar, o Android vai perguntar se confia no app.

**Se aparecer "Instalar apps desconhecidos":**

1. Toque em **"Configurações"** quando aparecer a mensagem
2. Ative a chave **"Permitir desta fonte"**
3. Volte e toque novamente no arquivo baixado

**Ou faça manualmente:**

1. Abra **Configurações** do celular
2. Procure por **"Segurança"** ou **"Aplicativos"**
3. Encontre **"Instalar apps desconhecidos"** ou **"Fontes desconhecidas"**
4. Permita para o **Chrome** (ou navegador que usou)

**🔒 Isso é seguro?**  
✅ Sim! O Android só bloqueia porque o app não veio da Play Store. Mas você baixou do código oficial no GitHub.

---

### ✅ PASSO 3: Instalar e Usar

1. Abra a pasta **Downloads** no seu celular
2. Toque no arquivo **app-debug.apk**
3. Toque em **"Instalar"**
4. Aguarde uns 10 segundos
5. Toque em **"Abrir"**

**Na primeira vez que abrir:**

1. O app vai pedir **"Permitir localização"** → Toque em **"Permitir"** ou **"Ao usar o app"**
2. O app vai pedir **"Permitir câmera"** → Toque em **"Permitir"**
3. Pronto! O mapa vai aparecer mostrando onde você está! 🗺️

---

## 🎉 Tudo Funcionando!

Agora você vai ver:

- 🗺️ **Um mapa** mostrando sua localização
- 📍 **Um ponto azul** indicando onde você está
- ➕ **Um botão redondo** para adicionar amostras

**Como usar:**

1. Vá até o local onde quer coletar a amostra
2. Toque no botão **+** (redondo, no canto)
3. Tire uma foto da amostra
4. Escreva uma observação (opcional)
5. Toque em **"Salvar"**
6. Pronto! Sua amostra foi salva! ✅

**💾 Funciona sem internet!**  
Todas as amostras ficam salvas no celular. Quando você tiver Wi-Fi ou dados móveis, elas sincronizam automaticamente com a nuvem.

---

## 🔧 Opção 2: Gerar Seu Próprio APK

**Para quem quer compilar do zero ou customizar.**

### Pré-requisitos

- Computador com Android Studio instalado
- Projeto AgroColetor baixado

### Passo 1: Abrir o Projeto

```bash
# Baixar o projeto
git clone https://github.com/SauloRodrigues20/Agrogeocolector.git
cd Agrogeocolector

# Configurar credenciais
cp local.properties.example local.properties
# Editar local.properties com suas credenciais Supabase
```

### Passo 2: Gerar APK Debug

No Android Studio:

1. Clique em **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
2. Aguarde a compilação (~2-3 minutos)
3. Clique em **"locate"** quando aparecer a notificação
4. O APK estará em: `app/build/outputs/apk/debug/app-debug.apk`

**Ou via terminal:**

```bash
# No terminal do projeto
./gradlew assembleDebug

# APK gerado em:
# app/build/outputs/apk/debug/app-debug.apk
```

### Passo 3: Gerar APK Release (Otimizado)

**Para distribuição pública:**

```bash
# Gerar APK release
./gradlew assembleRelease

# APK gerado em:
# app/build/outputs/apk/release/app-release-unsigned.apk
```

⚠️ **Nota**: O APK release precisa ser assinado para instalar. Veja seção "Assinar APK" abaixo.

### Passo 4: Transferir para o Celular

**Opção A: USB**

1. Conecte o celular no computador (cabo USB)
2. Copie o APK para a pasta **Downloads** do celular
3. Desconecte o cabo

**Opção B: Google Drive / Dropbox**

1. Faça upload do APK para a nuvem
2. No celular, baixe o arquivo

**Opção C: Email**

1. Envie o APK por email para você mesmo
2. Abra no celular e baixe o anexo

### Passo 5: Instalar no Celular

1. Abra o app **Arquivos** ou **Downloads** no celular
2. Encontre o arquivo `app-debug.apk`
3. Toque no arquivo
4. Toque em **"Instalar"**
5. Se pedir permissão de "fontes desconhecidas", permita
6. Aguarde e toque em **"Abrir"**

---

## 🔐 Assinar APK (Para Produção)

### Criar Keystore

```bash
# Criar uma keystore (uma única vez)
keytool -genkey -v -keystore agrogeocolector.keystore \
  -alias agrogeocolector -keyalg RSA -keysize 2048 -validity 10000

# Preencha as informações solicitadas
# GUARDE A SENHA COM SEGURANÇA!
```

### Assinar o APK

```bash
# Assinar o APK release
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 \
  -keystore agrogeocolector.keystore \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  agrogeocolector

# Otimizar (zipalign)
zipalign -v 4 \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  app/build/outputs/apk/release/AgroColetor-v1.0.0.apk
```

### Ou Configurar no build.gradle.kts

Adicione em `app/build.gradle.kts`:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../agrogeocolector.keystore")
            storePassword = "sua-senha-aqui"
            keyAlias = "agrogeocolector"
            keyPassword = "sua-senha-aqui"
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ... resto das configurações
        }
    }
}
```

Depois:

```bash
./gradlew assembleRelease
# APK assinado em: app/build/outputs/apk/release/app-release.apk
```

---

## 📤 Distribuir o APK

### Opção 1: GitHub Releases (Recomendado)

1. Vá em: https://github.com/SauloRodrigues20/Agrogeocolector/releases
2. Clique em **"Create a new release"**
3. Preencha:
   - **Tag**: v1.0.0
   - **Title**: AgroColetor v1.0.0
   - **Description**: Changelog das novidades
4. Faça upload do APK assinado
5. Clique em **"Publish release"**

### Opção 2: Google Drive

1. Faça upload do APK para o Google Drive
2. Defina permissão: **"Qualquer pessoa com o link"**
3. Copie o link
4. Compartilhe o link

### Opção 3: Telegram / WhatsApp

1. Envie o APK em um grupo ou canal
2. Pessoas podem baixar diretamente

### Opção 4: Seu Próprio Site

1. Faça upload em seu servidor
2. Link direto: `https://seusite.com/downloads/AgroColetor-v1.0.0.apk`

---

## ⚙️ Personalizar Antes de Gerar

### Mudar Nome do App

Em `app/src/main/res/values/strings.xml`:

```xml
<string name="app_name">Meu AgroColetor</string>
```

### Mudar Ícone

Substitua os arquivos em:
- `app/src/main/res/mipmap-mdpi/ic_launcher.png`
- `app/src/main/res/mipmap-hdpi/ic_launcher.png`
- `app/src/main/res/mipmap-xhdpi/ic_launcher.png`
- `app/src/main/res/mipmap-xxhdpi/ic_launcher.png`
- `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png`

### Mudar Versão

Em `app/build.gradle.kts`:

```kotlin
defaultConfig {
    versionCode = 2        // Incrementar a cada versão
    versionName = "1.1.0"  // Versão legível
}
```

---

## ❓ Perguntas e Respostas

### 📱 Funciona no meu celular?

**Funciona se seu Android for versão 8.0 ou mais nova.**

Como saber:
1. Abra **Configurações**
2. Vá em **"Sobre o telefone"**
3. Veja a **"Versão do Android"**
4. Se for 8, 9, 10, 11, 12, 13, 14... funciona! ✅

Funciona em: Samsung, Xiaomi, Motorola, LG, Asus, etc.

---

### 🌐 Precisa de internet?

**Para usar o app: NÃO!** 🎉

- ✅ O mapa funciona offline
- ✅ Você tira fotos offline
- ✅ As amostras são salvas no celular

**Para sincronizar: SIM (mas é automático)**

- 🔄 Quando você tiver Wi-Fi ou dados móveis
- 🔄 O app envia automaticamente para a nuvem
- 🔄 Você nem precisa fazer nada!

**Resumindo:** Funciona no campo sem sinal! Sincroniza quando voltar pra cidade.

---

### 🔒 É seguro?

**Sim! 100% seguro!** ✅

- ✅ Código aberto (qualquer um pode ver)
- ✅ Sem vírus, sem malware
- ✅ Não rouba seus dados
- ✅ Baixado do GitHub (site oficial de desenvolvedores)

O Android só bloqueia porque não veio da Play Store, mas isso não significa que é perigoso.

**⚠️ Importante:** Só baixe do link oficial:  
https://github.com/SauloRodrigues20/Agrogeocolector/releases

Nunca baixe de sites desconhecidos!

---

### 💾 Quanto espaço ocupa?

**~30 MB** (menos que a maioria dos apps)

- WhatsApp: ~150 MB
- Instagram: ~200 MB
- AgroColetor: ~30 MB ✅

As fotos que você tirar vão ocupar espaço extra (normal).

---

### 🔄 Como atualizar depois?

**Super fácil!**

1. Baixe a nova versão (mesmo jeito)
2. Instale por cima (não precisa desinstalar)
3. Suas amostras continuam salvas! ✅

---

### 🗑️ Como desinstalar?

**Igual qualquer app:**

1. Configurações → Apps
2. Encontre **AgroColetor**
3. Toque em **"Desinstalar"**

---

### 📸 As fotos ficam na galeria?

**Não!** As fotos ficam guardadas dentro do app.

- ✅ Não bagunça sua galeria
- ✅ Não mistura com suas fotos pessoais
- ✅ Só aparecem dentro do AgroColetor

---

### 🌍 Funciona em qualquer lugar do Brasil?

**Sim!** 🇧🇷

- ✅ Todo o Brasil
- ✅ Zona rural
- ✅ Cidades
- ✅ Qualquer fazenda

O mapa funciona em qualquer lugar do mundo!

---

### 📱 Funciona no iPhone?

**Não.** ❌

Este app é só para Android. iPhone usa outro sistema (iOS) e não aceita arquivos .apk.

---

### 💸 Quanto custa?

**R$ 0,00 - 100% GRATUITO!** 🎉

- ✅ Sem anúncios
- ✅ Sem compras no app
- ✅ Sem mensalidade
- ✅ Sem pegadinhas

Totalmente grátis para sempre!

---

### 🔋 Gasta muita bateria?

**Não!** O app é otimizado:

- 🔋 Só usa GPS quando você está usando
- 🔋 Não fica rodando em segundo plano
- 🔋 Sincronização rápida e eficiente

---

### 📍 Funciona sem GPS?

**Não.** Você precisa de GPS ligado para:

- Marcar a localização das amostras
- Ver sua posição no mapa

Mas não precisa de internet! GPS funciona offline.

---

### ❌ O app travou, o que fazer?

1. Feche completamente o app
2. Abra novamente
3. Se continuar travando, reporte aqui:  
   https://github.com/SauloRodrigues20/Agrogeocolector/issues

---

### 📞 Onde pedir ajuda?

Se tiver problemas:

1. Leia este guia novamente (solução pode estar aqui)
2. Veja as **Issues** no GitHub:  
   https://github.com/SauloRodrigues20/Agrogeocolector/issues
3. Abra uma nova **Issue** explicando o problema
4. A comunidade vai te ajudar!

---

## 🔄 Atualizar o App

### Se instalou via APK:

1. Baixe a nova versão (ex: v1.1.0)
2. Instale por cima (não precisa desinstalar)
3. Seus dados serão mantidos

### Se quer limpar tudo:

1. Desinstale o app
2. Instale a nova versão
3. Configure novamente

---

## 📦 Tamanhos dos APKs

| Tipo | Tamanho | Quando usar |
|------|---------|-------------|
| **Debug** | ~35 MB | Testes e desenvolvimento |
| **Release** | ~25 MB | Distribuição para usuários |
| **Release + ProGuard** | ~15 MB | Produção (otimizado) |

---

## 🎬 Vídeo Tutorial (Em Breve)

Em breve teremos um vídeo no YouTube mostrando passo a passo!

---

## 🚀 Resumo Ultra Rápido

```
1️⃣  Baixar app-debug.apk do GitHub
2️⃣  Permitir instalação (só na primeira vez)
3️⃣  Instalar
4️⃣  Abrir e permitir GPS + Câmera
5️⃣  Usar! 🎉
```

**⏱️ Total: 5 minutos**

---

## 📱 Links Importantes

| Link | Descrição |
|------|-----------|
| [**📥 BAIXAR APP**](https://github.com/SauloRodrigues20/Agrogeocolector/releases) | Baixe aqui o APK |
| [Código Fonte](https://github.com/SauloRodrigues20/Agrogeocolector) | Veja todo o código |
| [Reportar Problema](https://github.com/SauloRodrigues20/Agrogeocolector/issues) | App não funciona? |
| [Fazer Pergunta](https://github.com/SauloRodrigues20/Agrogeocolector/discussions) | Tire suas dúvidas |

---

## 🎯 Checklist de Instalação

Marque conforme vai fazendo:

- [ ] Baixei o arquivo .apk
- [ ] Permiti instalar apps desconhecidos
- [ ] Instalei o app
- [ ] Abri o app
- [ ] Permiti acesso à localização
- [ ] Permiti acesso à câmera
- [ ] Vi o mapa aparecer
- [ ] Testei adicionar uma amostra
- [ ] ✅ **Tudo funcionando!**

---

## 💡 Dicas Úteis

### 📍 Melhorar Precisão do GPS

1. Use o app ao ar livre (não dentro de casa)
2. Aguarde uns 30 segundos para GPS estabilizar
3. Modo avião? Desligue! GPS precisa estar ativo

### 📸 Tirar Boas Fotos

1. Limpe a lente da câmera
2. Tire foto com boa luz (evite sombras)
3. Foque na amostra (toque na tela para focar)

### 💾 Economizar Espaço

1. O app já comprime as fotos automaticamente
2. Você pode deletar amostras antigas
3. Sincronize e depois limpe localmente

### 🔋 Economizar Bateria

1. Feche o app quando não estiver usando
2. Sincronize quando chegar em casa (Wi-Fi)
3. Não deixe o mapa aberto sem usar

---

## 🤝 Ajude o Projeto

Gostou do app? Ajude outros agrônomos:

- ⭐ Dê uma estrela no GitHub
- 📢 Compartilhe com colegas
- 💬 Conte sua experiência
- 🐛 Reporte bugs
- 💡 Sugira melhorias

---

## 📞 Suporte

**Problemas com instalação?**

1. Releia este guia com calma
2. Confira se seu Android é 8.0+
3. Veja se tem espaço no celular (30 MB livres)
4. Tente desinstalar e instalar novamente

**Ainda com problema?**

Abra uma Issue: https://github.com/SauloRodrigues20/Agrogeocolector/issues

---

**🌱 Instale agora e revolucione sua coleta de amostras de solo! 🇧🇷**

**Desenvolvido com ❤️ para a Agronomia Brasileira**
