# 🚀 Como Publicar uma Nova Versão

## ⚡ Processo Automatizado

O projeto usa GitHub Actions para gerar APKs automaticamente!

---

## 📋 Passo a Passo

### 1. Preparar Nova Versão

#### 1.1. Atualizar Versão no Código

Edite `app/build.gradle.kts`:

```kotlin
defaultConfig {
    versionCode = 2        // Incrementar: 1, 2, 3, ...
    versionName = "1.1.0"  // Seguir Semantic Versioning
}
```

**Semantic Versioning:**
- `1.0.0` → `1.0.1` - Bug fixes (patch)
- `1.0.0` → `1.1.0` - Novas funcionalidades (minor)
- `1.0.0` → `2.0.0` - Mudanças incompatíveis (major)

#### 1.2. Atualizar CHANGELOG

Crie ou edite `CHANGELOG.md`:

```markdown
# Changelog

## [1.1.0] - 2025-12-11

### Adicionado
- Nova tela de lista de amostras
- Filtros por data e fazenda

### Corrigido
- Bug na sincronização
- Crash ao tirar foto

### Melhorado
- Performance do mapa
- Compressão de imagens
```

#### 1.3. Commitar Mudanças

```bash
git add app/build.gradle.kts CHANGELOG.md
git commit -m "chore: bump version to 1.1.0"
git push origin main
```

---

### 2. Criar Tag

```bash
# Criar tag anotada
git tag -a v1.1.0 -m "Release v1.1.0

✨ Novas funcionalidades:
- Tela de lista de amostras
- Filtros avançados

🐛 Correções:
- Bug na sincronização
- Crash ao tirar foto

⚡ Melhorias:
- Performance do mapa
- Compressão de imagens
"

# Enviar tag para o GitHub
git push origin v1.1.0
```

---

### 3. GitHub Actions Automático

Assim que você fizer `git push origin v1.1.0`:

1. ✅ GitHub Actions será acionado automaticamente
2. ✅ Projeto será compilado
3. ✅ APKs serão gerados (debug + release)
4. ✅ Release será criada automaticamente
5. ✅ APKs serão anexados ao Release

**Acompanhe em:**
https://github.com/SauloRodrigues20/Agrogeocolector/actions

---

### 4. Editar Release (Opcional)

1. Acesse: https://github.com/SauloRodrigues20/Agrogeocolector/releases
2. Clique em **"Edit"** na release criada
3. Melhore a descrição se quiser
4. Adicione screenshots
5. Salve

---

## 🔧 Processo Manual (Se Necessário)

### Gerar APK Localmente

```bash
# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease

# APKs estarão em:
# app/build/outputs/apk/debug/app-debug.apk
# app/build/outputs/apk/release/app-release-unsigned.apk
```

### Criar Release Manual

1. Vá em: https://github.com/SauloRodrigues20/Agrogeocolector/releases/new
2. Preencha:
   - **Tag**: v1.1.0
   - **Title**: AgroColetor v1.1.0
   - **Description**: Copie do CHANGELOG.md
3. Faça upload dos APKs
4. Clique em **"Publish release"**

---

## 📝 Template de Descrição do Release

```markdown
# 🌱 AgroColetor v1.1.0

## 📥 Como Instalar

1. Baixe o arquivo `app-debug.apk` abaixo
2. Transfira para seu celular Android
3. Instale (permita "fontes desconhecidas")
4. Veja o guia completo: [INSTALACAO_APK.md](INSTALACAO_APK.md)

## ✨ Novidades

- 🎉 Nova tela de lista de amostras
- 🔍 Filtros por data e fazenda
- 📊 Estatísticas de coletas

## 🐛 Correções

- ✅ Corrigido bug na sincronização com Supabase
- ✅ Resolvido crash ao tirar foto em alguns dispositivos
- ✅ Melhorada estabilidade do mapa

## ⚡ Melhorias

- 🚀 Performance do mapa 30% mais rápida
- 📸 Compressão de imagens otimizada
- 🔋 Menor consumo de bateria

## 📋 Requisitos

- Android 8.0 (API 26) ou superior
- 2GB RAM mínimo
- GPS e câmera (recomendado)

## 🔗 Links Úteis

- 📖 [Documentação](https://github.com/SauloRodrigues20/Agrogeocolector)
- 🐛 [Reportar Bug](https://github.com/SauloRodrigues20/Agrogeocolector/issues)
- 💬 [Discussões](https://github.com/SauloRodrigues20/Agrogeocolector/discussions)

---

**🇧🇷 Desenvolvido para a Agronomia Brasileira**
```

---

## 🎯 Checklist de Release

Antes de publicar, verifique:

- [ ] Versão atualizada em `build.gradle.kts`
- [ ] CHANGELOG.md atualizado
- [ ] Testes executados com sucesso
- [ ] App testado em dispositivo real
- [ ] Commits enviados para o GitHub
- [ ] Tag criada e enviada
- [ ] GitHub Actions rodou sem erros
- [ ] Release aparece no GitHub
- [ ] APKs disponíveis para download
- [ ] Descrição do release clara
- [ ] Links da documentação funcionando

---

## 🔄 Versionamento

### versionCode (Número Inteiro)

```kotlin
versionCode = 1  // Primeira versão
versionCode = 2  // Segunda versão
versionCode = 3  // Terceira versão
// Sempre incrementar!
```

**Usado pelo Android para:**
- Detectar atualizações
- Ordem de versões

### versionName (String Legível)

```kotlin
versionName = "1.0.0"    // Primeira versão pública
versionName = "1.0.1"    // Bug fix
versionName = "1.1.0"    // Nova funcionalidade
versionName = "2.0.0"    // Grande atualização
```

**Usado para:**
- Mostrar para usuários
- Documentação

---

## 📊 Exemplo de Histórico

| versionCode | versionName | Data | Descrição |
|-------------|-------------|------|-----------|
| 1 | 1.0.0 | 2025-12-01 | Release inicial |
| 2 | 1.0.1 | 2025-12-05 | Bug fixes |
| 3 | 1.1.0 | 2025-12-10 | Nova tela de lista |
| 4 | 1.1.1 | 2025-12-12 | Correções |
| 5 | 2.0.0 | 2025-12-20 | Redesign completo |

---

## 🚀 Automatização Completa

O workflow `.github/workflows/build-apk.yml` faz:

✅ Compila o projeto automaticamente  
✅ Gera APK debug  
✅ Gera APK release  
✅ Cria release no GitHub  
✅ Anexa APKs automaticamente  
✅ Adiciona descrição padrão  

**Você só precisa:**
1. Atualizar versão
2. Fazer commit
3. Criar tag
4. Push!

---

**🎉 Processo de release automatizado e profissional!**
