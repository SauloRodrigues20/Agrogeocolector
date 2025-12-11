# 🏪 Como Publicar na Google Play Store

## 📋 Pré-requisitos

### 1. Conta de Desenvolvedor Google Play

**Custo:** US$ 25 (pagamento único, válido para sempre)

**Como criar:**

1. Acesse: https://play.google.com/console/signup
2. Faça login com sua conta Google
3. Pague a taxa de US$ 25
4. Preencha os dados do desenvolvedor
5. Aguarde aprovação (1-2 dias)

---

## 🔐 Passo 1: Assinar o APK

A Play Store só aceita APKs **assinados digitalmente**.

### Criar Keystore (Chave Digital)

```bash
# Criar keystore (GUARDE ESTA SENHA!)
keytool -genkey -v -keystore agrogeocolector.keystore \
  -alias agrogeocolector \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000

# Preencha as informações solicitadas:
# - Nome e sobrenome
# - Nome da organização
# - Cidade
# - Estado
# - País
# - Senha (ANOTE EM LOCAL SEGURO!)
```

**⚠️ MUITO IMPORTANTE:**
- Guarde o arquivo `.keystore` em local seguro
- Anote a senha em local seguro
- Se perder, NUNCA mais poderá atualizar o app na Play Store!

### Configurar build.gradle.kts

Adicione em `app/build.gradle.kts`:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../agrogeocolector.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "sua-senha"
            keyAlias = "agrogeocolector"
            keyPassword = System.getenv("KEY_PASSWORD") ?: "sua-senha"
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

### Gerar APK Assinado

```bash
# Gerar APK release assinado
./gradlew assembleRelease

# APK estará em:
# app/build/outputs/apk/release/app-release.apk
```

---

## 📦 Passo 2: Gerar Android App Bundle (AAB)

**A Play Store prefere AAB ao invés de APK!**

### O que é AAB?

- Formato moderno do Google
- Menor tamanho de download
- Otimização automática por dispositivo

### Gerar AAB

```bash
# Gerar bundle assinado
./gradlew bundleRelease

# AAB estará em:
# app/build/outputs/bundle/release/app-release.aab
```

---

## 🎨 Passo 3: Preparar Materiais Gráficos

### Ícone do App (obrigatório)

**Já temos!** Os ícones em `mipmap-*` estão prontos.

### Ícone da Play Store (512x512)

```python
# Criar ícone 512x512
from PIL import Image, ImageDraw, ImageFont

img = Image.new('RGB', (512, 512), '#4CAF50')
draw = ImageDraw.Draw(img)

try:
    font = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 320)
except:
    font = ImageFont.load_default()

text = "A"
bbox = draw.textbbox((0, 0), text, font=font)
text_width = bbox[2] - bbox[0]
text_height = bbox[3] - bbox[1]
position = ((512 - text_width) // 2, (512 - text_height) // 2 - bbox[1])

draw.text(position, text, fill='#FFFFFF', font=font)
img.save('play_store_icon.png')
print("✅ Ícone 512x512 criado!")
```

### Gráfico de Recurso (1024x500)

Banner para a página da Play Store:

```python
from PIL import Image, ImageDraw, ImageFont

img = Image.new('RGB', (1024, 500), '#4CAF50')
draw = ImageDraw.Draw(img)

try:
    font_title = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 80)
    font_subtitle = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 40)
except:
    font_title = font_subtitle = ImageFont.load_default()

# Título
draw.text((512, 180), "AgroColetor", fill='#FFFFFF', font=font_title, anchor="mm")
# Subtítulo
draw.text((512, 280), "Coleta de Amostras de Solo", fill='#FFFFFF', font=font_subtitle, anchor="mm")

img.save('play_store_feature_graphic.png')
print("✅ Gráfico de recurso criado!")
```

### Capturas de Tela (obrigatório)

**Requisitos:**
- Pelo menos 2 capturas de tela
- Tamanho: 320px a 3840px (largura ou altura)
- Formato: PNG ou JPG

**Como capturar:**

1. Rode o app no emulador
2. Navegue pelas telas principais
3. Use Ctrl+S ou botão de screenshot
4. Salve as imagens

**Telas recomendadas:**
- Mapa com localização
- Formulário de coleta
- Lista de amostras
- Tela de sincronização

---

## 📝 Passo 4: Informações da Listagem

### Título do App (máx 50 caracteres)

```
AgroColetor - Coleta de Solo
```

### Descrição Curta (máx 80 caracteres)

```
Coleta e georreferenciamento de amostras de solo com mapas offline
```

### Descrição Completa (máx 4000 caracteres)

```
🌱 AgroColetor - Revolucione a Coleta de Amostras de Solo

O AgroColetor é um aplicativo profissional para agrônomos, técnicos agrícolas e produtores rurais que realizam coleta de amostras de solo.

✨ FUNCIONALIDADES PRINCIPAIS:

📍 Georreferenciamento Preciso
• GPS de alta precisão
• Localização em tempo real
• Coordenadas geográficas automáticas

🗺️ Mapas Offline Gratuitos
• OpenStreetMap integrado
• Funciona sem internet no campo
• Visualização clara da propriedade

📸 Registro Fotográfico
• Capture fotos das amostras
• Compressão automática
• Organização por coleta

💾 Armazenamento Local
• Todas as coletas salvas no celular
• Funciona 100% offline
• Sem risco de perder dados

☁️ Sincronização Automática
• Envio automático para a nuvem
• Backup seguro no Supabase
• Acesso de qualquer lugar

📊 Organização Profissional
• Liste todas as coletas
• Busque por data ou localização
• Exporte dados facilmente

🎯 IDEAL PARA:

✓ Agrônomos e consultores
✓ Técnicos agrícolas
✓ Produtores rurais
✓ Estudantes de agronomia
✓ Empresas de análise de solo
✓ Cooperativas agrícolas

🆓 100% GRATUITO E OPEN SOURCE

• Sem anúncios
• Sem compras no app
• Sem mensalidades
• Código aberto no GitHub

📱 REQUISITOS:

• Android 8.0 ou superior
• GPS habilitado
• Câmera (para fotos)
• Conexão com internet (apenas para sincronização)

🔒 PRIVACIDADE E SEGURANÇA:

• Seus dados são criptografados
• Armazenamento seguro
• Você controla o que compartilha
• Sem rastreamento de usuários

🇧🇷 DESENVOLVIDO NO BRASIL

Criado especificamente para atender as necessidades da agronomia brasileira.

📞 SUPORTE:

• Reportar problemas: GitHub Issues
• Documentação: README completo
• Comunidade: GitHub Discussions

🌱 Baixe agora e modernize sua coleta de amostras de solo!
```

### Categoria

- **Produtividade** (primary)
- **Ferramentas** (secondary)

### Tags (palavras-chave)

```
agricultura, agronomia, solo, gps, mapa, coleta, georreferenciamento, campo, fazenda, offline
```

---

## 🚀 Passo 5: Criar Aplicativo no Console

1. Acesse: https://play.google.com/console
2. Clique em **"Criar app"**
3. Preencha:
   - **Nome do app:** AgroColetor
   - **Idioma padrão:** Português (Brasil)
   - **App ou jogo:** App
   - **Gratuito ou pago:** Gratuito
4. Aceite as declarações
5. Clique em **"Criar app"**

---

## 📤 Passo 6: Fazer Upload do AAB

### Na seção "Versões"

1. Vá em **Produção** → **Criar nova versão**
2. Faça upload do `app-release.aab`
3. Preencha:
   - **Nome da versão:** 1 (versionCode)
   - **Notas da versão:**
     ```
     🌱 Primeira versão do AgroColetor!
     
     ✨ Funcionalidades:
     • Mapas offline gratuitos
     • Georreferenciamento GPS
     • Captura de fotos
     • Sincronização automática
     • Armazenamento local seguro
     
     🇧🇷 Desenvolvido para a agronomia brasileira!
     ```
4. Clique em **"Salvar"**

---

## 🎨 Passo 7: Configurar Página da Loja

### Listagem Principal

1. Vá em **Listagem da loja principal**
2. Upload dos gráficos:
   - **Ícone do app:** play_store_icon.png (512x512)
   - **Gráfico de recursos:** play_store_feature_graphic.png (1024x500)
   - **Capturas de tela:** No mínimo 2 imagens
3. Preencha:
   - Título
   - Descrição curta
   - Descrição completa
4. Clique em **"Salvar"**

---

## 📋 Passo 8: Questionário de Conteúdo

1. Vá em **Classificação de conteúdo**
2. Responda o questionário:
   - **Categoria:** Ferramentas ou Referência
   - **Violência:** Não
   - **Conteúdo sexual:** Não
   - **Linguagem imprópria:** Não
   - **Drogas:** Não
   - **Outros:** Não
3. Submeta para revisão

---

## 🔒 Passo 9: Declarações e Políticas

### Política de Privacidade

Crie um arquivo `PRIVACY_POLICY.md` e hospede online (pode ser no GitHub):

```markdown
# Política de Privacidade - AgroColetor

**Última atualização:** 11/12/2025

## Coleta de Dados

O AgroColetor coleta apenas:
- Localização GPS (para georreferenciamento)
- Fotos capturadas pelo usuário
- Dados de amostras inseridos manualmente

## Uso dos Dados

Seus dados são usados exclusivamente para:
- Armazenar informações das coletas de solo
- Sincronizar entre dispositivos (opcional)
- Melhorar a experiência do usuário

## Compartilhamento

Não compartilhamos seus dados com terceiros.
Você tem controle total sobre suas informações.

## Armazenamento

Dados armazenados:
- Localmente no seu dispositivo
- No Supabase (se você optar por sincronizar)

## Seus Direitos

Você pode:
- Exportar todos os seus dados
- Deletar suas informações a qualquer momento
- Usar o app 100% offline

## Contato

Para dúvidas: https://github.com/SauloRodrigues20/Agrogeocolector/issues
```

Link para hospedar: `https://raw.githubusercontent.com/SauloRodrigues20/Agrogeocolector/main/PRIVACY_POLICY.md`

### Público-Alvo

- **Faixa etária:** 18+ (profissionais)

### Permissões

Marque as permissões que o app usa:
- ✅ Localização (GPS)
- ✅ Câmera (fotos)
- ✅ Armazenamento (salvar dados)
- ✅ Internet (sincronização)

---

## 📊 Passo 10: Configurar Testes (Opcional mas Recomendado)

### Teste Interno (mais rápido)

1. Vá em **Testes** → **Teste interno**
2. Crie uma lista de testadores
3. Adicione emails dos testadores
4. Faça upload de uma versão de teste
5. Compartilhe o link com os testadores
6. Receba feedback antes de lançar

### Teste Aberto/Fechado

Pule esta etapa se quiser lançar direto.

---

## ✅ Passo 11: Enviar para Revisão

1. Vá em **Painel**
2. Verifique se todas as seções estão completas (✅ verde)
3. Clique em **"Enviar para revisão"**

---

## ⏱️ Tempo de Análise

- **Primeira submissão:** 7-14 dias
- **Atualizações futuras:** 1-3 dias

**O que o Google analisa:**
- Funcionalidades do app
- Conteúdo da descrição
- Política de privacidade
- Permissões solicitadas
- Conformidade com políticas

---

## 🎉 Aprovação e Publicação

Quando aprovado:
1. Você recebe email de confirmação
2. O app aparece na Play Store em poucas horas
3. Link da Play Store: `https://play.google.com/store/apps/details?id=com.agrogeocolector`

---

## 🔄 Atualizações Futuras

### Processo Simplificado

```bash
# 1. Incrementar versão em app/build.gradle.kts
versionCode = 2
versionName = "1.1.0"

# 2. Gerar novo AAB
./gradlew bundleRelease

# 3. Upload no Console
# Play Console → Produção → Nova versão → Upload AAB

# 4. Preencher notas da versão
# "🔄 Atualização 1.1.0 - Novos recursos..."

# 5. Enviar para revisão
# Aprovação: 1-3 dias
```

---

## 💰 Custos

| Item | Valor | Frequência |
|------|-------|------------|
| Conta Desenvolvedor Google | US$ 25 | Uma vez |
| Hospedagem do app | R$ 0 | Grátis |
| Atualizações | R$ 0 | Grátis |
| **TOTAL** | **US$ 25** | **Vitalício** |

---

## ✅ Checklist Completo

**Antes de Submeter:**

- [ ] Conta Google Play criada e paga (US$ 25)
- [ ] Keystore criado e guardado em segurança
- [ ] APK/AAB assinado e gerado
- [ ] Ícone 512x512 criado
- [ ] Gráfico de recurso 1024x500 criado
- [ ] Pelo menos 2 capturas de tela
- [ ] Descrição completa escrita
- [ ] Política de privacidade hospedada online
- [ ] Questionário de conteúdo preenchido
- [ ] Todas as seções do Console completas (✅)

**Depois de Submeter:**

- [ ] Aguardar 7-14 dias (primeira vez)
- [ ] Responder eventuais dúvidas do Google
- [ ] Celebrar quando for aprovado! 🎉

---

## 🆘 Problemas Comuns

### "App rejeitado - Política de Privacidade"

**Solução:** Certifique-se que a URL da política está acessível e completa.

### "App rejeitado - Permissões"

**Solução:** Justifique cada permissão na descrição do app.

### "App rejeitado - Metadados"

**Solução:** Revise título, descrição e capturas de tela.

### "Build com erro"

**Solução:** Teste o AAB localmente antes de enviar:
```bash
bundletool build-apks --bundle=app-release.aab --output=test.apks
```

---

## 📚 Recursos Úteis

- [Documentação Oficial Google Play](https://developer.android.com/distribute/console)
- [Requisitos de Qualidade](https://developer.android.com/quality)
- [Políticas do Google Play](https://play.google.com/about/developer-content-policy/)
- [Bundletool (testar AAB)](https://developer.android.com/tools/bundletool)

---

## 🎯 Resumo Rápido

```
1. Criar conta Google Play (US$ 25)
2. Assinar o app com keystore
3. Gerar AAB (bundleRelease)
4. Criar materiais gráficos
5. Preencher informações da loja
6. Fazer upload do AAB
7. Configurar políticas e privacidade
8. Enviar para revisão
9. Aguardar 7-14 dias
10. Publicado! 🎉
```

**Tempo total:** 3-4 horas de trabalho + 7-14 dias de análise

**Depois de publicado:** Usuários instalam com 1 clique como qualquer app!

---

**🌱 Boa sorte com a publicação do AgroColetor! 🇧🇷**
